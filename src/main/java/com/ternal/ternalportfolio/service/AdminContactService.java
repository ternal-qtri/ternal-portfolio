package com.ternal.ternalportfolio.service;

import com.ternal.ternalportfolio.entity.ContactMessage;
import com.ternal.ternalportfolio.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminContactService {

    private final ContactMessageRepository contactRepo;

    public Page<ContactMessage> getContacts(String status, String keyword, int page, int size) {
        int validPage = Math.max(0, page);
        int validSize = size > 0 ? size : 10;
        Pageable pageable = PageRequest.of(validPage, validSize, Sort.by(Sort.Direction.DESC, "createdAt", "id"));

        String cleanStatus = (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL"))
                ? status.trim().toUpperCase()
                : null;
        String cleanKeyword = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;

        return contactRepo.findByStatusAndKeyword(cleanStatus, cleanKeyword, pageable);
    }

    public long getTotalCount() {
        return contactRepo.count();
    }

    public long getUnreadCount() {
        return contactRepo.countByStatus("UNREAD");
    }

    public long getRepliedCount() {
        return contactRepo.countByStatus("REPLIED");
    }

    public ContactMessage getContact(Long id) {
        return contactRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tin nhắn với ID: " + id));
    }

    @Transactional
    public void markAsRead(Long id) {
        ContactMessage contact = getContact(id);
        if ("UNREAD".equalsIgnoreCase(contact.getStatus())) {
            contact.setStatus("READ");
            contactRepo.save(contact);
        }
    }

    @Transactional
    public void markAsReplied(Long id) {
        ContactMessage contact = getContact(id);
        contact.setStatus("REPLIED");
        contactRepo.save(contact);
    }

    @Transactional
    public void deleteContact(Long id) {
        if (!contactRepo.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy tin nhắn với ID: " + id);
        }
        contactRepo.deleteById(id);
    }
}
