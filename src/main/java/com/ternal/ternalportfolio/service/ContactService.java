package com.ternal.ternalportfolio.service;

import com.ternal.ternalportfolio.entity.ContactMessage;
import com.ternal.ternalportfolio.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

    private final ContactMessageRepository contactRepo;
    private final EmailService emailService;

    @Transactional
    public void processContact(String fullName, String email, String subject, String message) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ và tên không được để trống.");
        }
        if (email == null || email.trim().isEmpty() || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Địa chỉ email không hợp lệ.");
        }
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Nội dung tin nhắn không được để trống.");
        }

        String cleanName = fullName.trim();
        String cleanEmail = email.trim();
        String cleanSubject = (subject != null && !subject.trim().isEmpty()) ? subject.trim() : "Liên hệ từ Portfolio";
        String cleanMessage = message.trim();

        ContactMessage contactMessage = ContactMessage.builder()
                .senderName(cleanName)
                .email(cleanEmail)
                .subject(cleanSubject)
                .message(cleanMessage)
                .status("UNREAD")
                .build();

        contactRepo.save(contactMessage);
        log.info("Đã lưu tin nhắn liên hệ từ {} ({}) vào CSDL", cleanName, cleanEmail);

        // Gửi email xác nhận đến khách hàng (Bất đồng bộ)
        emailService.sendContactSuccessEmail(cleanName, cleanEmail, cleanSubject, cleanMessage);

        // Gửi email thông báo đến Admin (Bất đồng bộ)
        emailService.sendAdminContactNotificationEmail(cleanName, cleanEmail, cleanSubject, cleanMessage);
    }
}
