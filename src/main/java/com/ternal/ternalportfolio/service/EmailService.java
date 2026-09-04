package com.ternal.ternalportfolio.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class EmailService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Value("${resend.api.key:${RESEND_API_KEY:}}")
    private String resendApiKey;

    @Value("${resend.from.email:${RESEND_FROM_EMAIL:Nguyễn Quốc Trí <onboarding@resend.dev>}}")
    private String fromEmail;

    @Value("${resend.reply-to.email:${EMAIL_USERNAME:ternal.qtri@gmail.com}}")
    private String defaultReplyToEmail;

    @Value("${admin.notification.email:ternal.qtri@gmail.com}")
    private String adminEmail;

    /**
     * Gửi email thông qua Resend REST API (HTTPS Cổng 443 - không bao giờ bị chặn trên Cloud / Render)
     */
    private void sendEmailViaResend(String to, String replyTo, String subject, String plainText, String htmlContent, Map<String, String> customHeaders) {
        if (resendApiKey == null || resendApiKey.trim().isEmpty()) {
            log.error("Chưa cấu hình RESEND_API_KEY. Không thể gửi email đến {}", to);
            return;
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("from", fromEmail);
            payload.put("to", List.of(to));
            payload.put("subject", subject);
            payload.put("text", plainText);
            payload.put("html", htmlContent);
            if (replyTo != null && !replyTo.isBlank()) {
                payload.put("reply_to", replyTo);
            }
            if (customHeaders != null && !customHeaders.isEmpty()) {
                payload.put("headers", customHeaders);
            }

            String jsonPayload = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey.trim())
                    .header("Content-Type", "application/json; charset=UTF-8")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .timeout(Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Email đã được gửi thành công qua Resend API đến: {}. Phản hồi: {}", to, response.body());
            } else {
                log.error("Resend API trả về lỗi khi gửi đến {} (HTTP {}): {}", to, response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("Ngoại lệ khi gọi Resend API gửi thư đến {}: {}", to, e.getMessage(), e);
        }
    }

    @Async
    public void sendContactSuccessEmail(String name, String toEmail, String subject, String message) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/client/email/contact-success.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            String formattedMessage = HtmlUtils.htmlEscape(message).replace("\r\n", "<br/>").replace("\n", "<br/>");
            String formattedTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy"));
            String displaySubject = (subject != null && !subject.trim().isEmpty()) ? HtmlUtils.htmlEscape(subject.trim()) : "Không có tiêu đề";

            String htmlContent = template
                    .replace("{{name}}", HtmlUtils.htmlEscape(name))
                    .replace("{{email}}", HtmlUtils.htmlEscape(toEmail))
                    .replace("{{subject}}", displaySubject)
                    .replace("{{submittedAt}}", formattedTime)
                    .replace("{{message}}", formattedMessage)
                    .replace("{{portfolioUrl}}", "https://ternal-nguyenquoctri.io.vn");

            // Phiên bản văn bản thuần (Plain Text) cho multipart/alternative chống bộ lọc spam
            String plainText = "Xin chào " + name + ",\n\n"
                    + "Cảm ơn bạn đã gửi tin nhắn liên hệ. Tôi đã tiếp nhận thành công thông điệp của bạn từ Portfolio cá nhân.\n\n"
                    + "THÔNG TIN CHI TIẾT:\n"
                    + "- Người gửi: " + name + "\n"
                    + "- Email: " + toEmail + "\n"
                    + "- Tiêu đề: " + displaySubject + "\n"
                    + "- Thời gian gửi: " + formattedTime + "\n"
                    + "- Nội dung:\n" + message.trim() + "\n\n"
                    + "Tôi sẽ trực tiếp xem xét và phản hồi lại bạn qua email sớm nhất (trong vòng 24 giờ làm việc).\n\n"
                    + "Trân trọng,\n"
                    + "Nguyễn Quốc Trí (Ternal)\n"
                    + "Backend Developer • Java / Spring Boot\n"
                    + "Website: https://ternal-nguyenquoctri.io.vn";

            Map<String, String> headers = Map.of(
                    "Auto-Submitted", "auto-replied",
                    "X-Auto-Response-Suppress", "All"
            );

            sendEmailViaResend(
                    toEmail,
                    defaultReplyToEmail,
                    "Nguyễn Quốc Trí: Xác nhận đã nhận được tin nhắn từ bạn",
                    plainText,
                    htmlContent,
                    headers
            );
        } catch (Exception e) {
            log.error("Không thể xử lý dữ liệu gửi email xác nhận đến khách hàng {}: {}", toEmail, e.getMessage(), e);
        }
    }

    @Async
    public void sendAdminContactNotificationEmail(String name, String senderEmail, String subject, String message) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/client/email/contact-admin-notification.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            String formattedMessage = HtmlUtils.htmlEscape(message).replace("\r\n", "<br/>").replace("\n", "<br/>");
            String formattedTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy"));
            String displaySubject = (subject != null && !subject.trim().isEmpty()) ? HtmlUtils.htmlEscape(subject.trim()) : "Không có tiêu đề";

            String htmlContent = template
                    .replace("{{name}}", HtmlUtils.htmlEscape(name))
                    .replace("{{email}}", HtmlUtils.htmlEscape(senderEmail))
                    .replace("{{subject}}", displaySubject)
                    .replace("{{submittedAt}}", formattedTime)
                    .replace("{{message}}", formattedMessage)
                    .replace("{{adminUrl}}", "https://ternal-nguyenquoctri.io.vn/admin/contacts")
                    .replace("{{replyUrl}}", "mailto:" + HtmlUtils.htmlEscape(senderEmail) + "?subject=Re: " + displaySubject);

            String plainText = "THÔNG BÁO LIÊN HỆ MỚI:\n\n"
                    + "- Người gửi: " + name + "\n"
                    + "- Email: " + senderEmail + "\n"
                    + "- Tiêu đề: " + displaySubject + "\n"
                    + "- Thời gian: " + formattedTime + "\n"
                    + "- Nội dung:\n" + message.trim() + "\n\n"
                    + "Xem chi tiết tại: https://ternal-nguyenquoctri.io.vn/admin/contacts";

            Map<String, String> headers = Map.of(
                    "X-Auto-Response-Suppress", "All"
            );

            String replyTo = (senderEmail != null && !senderEmail.isBlank()) ? senderEmail : defaultReplyToEmail;

            sendEmailViaResend(
                    adminEmail,
                    replyTo,
                    "[Liên hệ mới] " + name + ": " + displaySubject,
                    plainText,
                    htmlContent,
                    headers
            );
        } catch (Exception e) {
            log.error("Không thể xử lý dữ liệu gửi email thông báo liên hệ mới đến Admin ({}): {}", adminEmail, e.getMessage(), e);
        }
    }
}
