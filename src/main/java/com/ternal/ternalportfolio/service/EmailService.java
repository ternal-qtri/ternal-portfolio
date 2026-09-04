package com.ternal.ternalportfolio.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:ternal.qtri@gmail.com}")
    private String fromEmail;

    @Value("${admin.notification.email:ternal.qtri@gmail.com}")
    private String adminEmail;

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

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            helper.setFrom(fromEmail, "Nguyễn Quốc Trí");
            helper.setTo(toEmail);
            helper.setReplyTo(fromEmail, "Nguyễn Quốc Trí");
            helper.setSubject("Nguyễn Quốc Trí: Xác nhận đã nhận được tin nhắn từ bạn");
            
            // Gửi cả bản text thuần và bản HTML chuẩn MIME multipart/alternative
            helper.setText(plainText, htmlContent);

            // Bổ sung header xác nhận phản hồi tự động chuẩn RFC 3834
            mimeMessage.addHeader("Auto-Submitted", "auto-replied");
            mimeMessage.addHeader("X-Auto-Response-Suppress", "All");

            mailSender.send(mimeMessage);
            log.info("Email xác nhận liên hệ đã được gửi thành công đến khách hàng: {}", toEmail);
        } catch (Exception e) {
            log.error("Không thể gửi email xác nhận đến khách hàng {}: {}", toEmail, e.getMessage(), e);
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

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            helper.setFrom(fromEmail, "Portfolio Notification");
            helper.setTo(adminEmail);
            helper.setReplyTo(senderEmail, name);
            helper.setSubject("[Liên hệ mới] " + name + ": " + displaySubject);
            helper.setText(plainText, htmlContent);

            mimeMessage.addHeader("X-Auto-Response-Suppress", "All");

            mailSender.send(mimeMessage);
            log.info("Email thông báo liên hệ mới đã được gửi thành công đến Admin: {}", adminEmail);
        } catch (Exception e) {
            log.error("Không thể gửi email thông báo liên hệ mới đến Admin ({}): {}", adminEmail, e.getMessage(), e);
        }
    }
}
