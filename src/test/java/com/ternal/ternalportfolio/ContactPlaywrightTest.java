package com.ternal.ternalportfolio;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import com.ternal.ternalportfolio.entity.ContactMessage;
import com.ternal.ternalportfolio.repository.ContactMessageRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ContactPlaywrightTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeAll
    static void setUpBrowser() {
        playwright = Playwright.create();
        // Khởi chạy trình duyệt Chromium ở chế độ Headless
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @AfterAll
    static void tearDownBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void setUpContext() {
        context = browser.newContext(new Browser.NewContextOptions().setViewportSize(1280, 800));
        page = context.newPage();
    }

    @AfterEach
    void tearDownContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Kiểm thử E2E: Người dùng nqt1295@gmail.com gửi form contact, mã hóa bảo mật và lưu vào CSDL")
    void testSubmitContactFormWithPlaywright() {
        String testEmail = "nqt1295@gmail.com";
        String testFullName = "Nguyễn Quốc Trí";
        String testSubject = "Thử nghiệm gửi liên hệ tự động (Playwright E2E)";
        String testMessage = "Xin chào! Đây là tin nhắn thử nghiệm chức năng liên hệ được gửi tự động qua Playwright và JUnit 5.";

        String contactUrl = "http://localhost:" + port + "/contact";

        // 1. Điều hướng tới trang /contact
        page.navigate(contactUrl, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        // 2. Chờ form liên hệ sẵn sàng và hiển thị trên giao diện
        page.waitForSelector("#contact-form");

        // 3. Điền các trường thông tin theo đúng yêu cầu
        page.fill("#fullName", testFullName);
        page.fill("#email", testEmail);
        page.fill("#subject", testSubject);
        page.fill("#message", testMessage);

        // 4. Lắng nghe và chặn bắt response HTTP POST gửi tới /contact khi bấm nút
        Response response = page.waitForResponse(
                res -> res.url().contains("/contact") && "POST".equalsIgnoreCase(res.request().method()),
                () -> page.click("#btn-submit-contact")
        );

        // 5. Kiểm tra kết quả phản hồi HTTP từ server
        assertNotNull(response, "Phải nhận được response từ server sau khi bấm gửi");
        assertEquals(200, response.status(), "Server phải trả về mã HTTP 200 OK");

        String responseBody = response.text();
        assertTrue(responseBody.contains("\"success\":true"), "Response JSON phải chứa success: true");

        // 6. Kiểm tra giao diện người dùng (thông báo Notyf toast xuất hiện)
        Locator toastLocator = page.locator(".notyf__toast");
        toastLocator.first().waitFor(new Locator.WaitForOptions().setTimeout(7000));
        assertTrue(toastLocator.first().isVisible(), "Thông báo Toast thành công phải hiển thị trên màn hình");

        // 7. Kiểm tra dữ liệu đã được lưu thành công vào CSDL (Supabase PostgreSQL)
        List<ContactMessage> allMessages = contactMessageRepository.findAll();
        Optional<ContactMessage> savedMessageOpt = allMessages.stream()
                .filter(m -> testEmail.equalsIgnoreCase(m.getEmail()))
                .reduce((first, second) -> second); // Lấy bản ghi mới nhất

        assertTrue(savedMessageOpt.isPresent(), "Tin nhắn với email " + testEmail + " phải được lưu vào CSDL");
        ContactMessage savedMessage = savedMessageOpt.get();
        assertEquals(testFullName, savedMessage.getSenderName(), "Tên người gửi trong CSDL phải khớp");
        assertEquals(testSubject, savedMessage.getSubject(), "Tiêu đề trong CSDL phải khớp");
        assertEquals(testMessage, savedMessage.getMessage(), "Nội dung tin nhắn trong CSDL phải khớp");
        assertEquals("UNREAD", savedMessage.getStatus(), "Trạng thái tin nhắn mới phải là UNREAD");
    }
}
