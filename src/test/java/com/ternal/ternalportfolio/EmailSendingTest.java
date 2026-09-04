package com.ternal.ternalportfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ternal.ternalportfolio.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class EmailSendingTest {

    @Autowired
    private EmailService emailService;

    @Value("${resend.api.key:${RESEND_API_KEY:}}")
    private String resendApiKey;

    @Value("${resend.from.email:${RESEND_FROM_EMAIL:Nguyen Quoc Tri <onboarding@resend.dev>}}")
    private String fromEmail;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Kiểm tra trực tiếp việc gửi email qua Resend tới nqt1295@gmail.com")
    void testDirectResendToNqt1295() throws Exception {
        String testRecipient = "nqt1295@gmail.com";
        System.out.println("================================================================================");
        System.out.println("THỬ NGHIỆM GỬI EMAIL THỰC TẾ QUA RESEND API TỚI: " + testRecipient);
        System.out.println("API Key: " + (resendApiKey != null && !resendApiKey.isEmpty() ? resendApiKey.substring(0, 7) + "..." : "CHƯA CÓ"));
        System.out.println("From: " + fromEmail);
        System.out.println("================================================================================");

        assertNotNull(resendApiKey, "RESEND_API_KEY không được để trống");

        // 1. Kiểm tra gọi qua EmailService của hệ thống
        System.out.println("\n--- [Bước 1]: Kích hoạt EmailService.sendContactSuccessEmail() ---");
        emailService.sendContactSuccessEmail(
                "Nguyễn Quốc Trí",
                testRecipient,
                "Kiểm tra chức năng liên hệ",
                "Nội dung kiểm tra gửi email tự động tới " + testRecipient
        );

        System.out.println("\n--- [Bước 2]: Kích hoạt EmailService.sendAdminContactNotificationEmail() ---");
        emailService.sendAdminContactNotificationEmail(
                "Nguyễn Quốc Trí",
                testRecipient,
                "Kiểm tra thông báo admin",
                "Khách hàng " + testRecipient + " vừa gửi tin nhắn liên hệ."
        );

        // Chờ 3 giây để background async thread thực hiện cuộc gọi mạng tới Resend
        Thread.sleep(3000);

        // 2. Gọi trực tiếp HTTP tới Resend API để in chi tiết response trả về
        System.out.println("\n--- [Bước 3]: Gửi trực tiếp tới Resend API và đọc Response từ máy chủ Resend ---");
        Map<String, Object> payload = Map.of(
                "from", fromEmail,
                "to", List.of(testRecipient),
                "subject", "[TEST JUNIT] Kiểm tra gửi mail tới " + testRecipient,
                "html", "<p>Chào bạn, đây là email kiểm thử hệ thống gửi tới <strong>" + testRecipient + "</strong>.</p>"
        );

        String json = objectMapper.writeValueAsString(payload);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.resend.com/emails"))
                .header("Authorization", "Bearer " + resendApiKey.trim())
                .header("Content-Type", "application/json; charset=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .timeout(Duration.ofSeconds(15))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("HTTP Status Code từ Resend: " + response.statusCode());
        System.out.println("Response Body từ Resend: " + response.body());
        System.out.println("================================================================================");
    }
}
