package com.ternal.ternalportfolio.controller;

import com.ternal.ternalportfolio.service.AdminContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/admin/contacts")
@RequiredArgsConstructor
public class AdminContactController {

    private final AdminContactService contactService;

    @GetMapping
    public String list(
            @RequestParam(required = false, defaultValue = "ALL") String status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            Model model) {
        model.addAttribute("contacts", contactService.getContacts(status, search, page, 10));
        model.addAttribute("totalCount", contactService.getTotalCount());
        model.addAttribute("unreadCount", contactService.getUnreadCount());
        model.addAttribute("repliedCount", contactService.getRepliedCount());
        model.addAttribute("selectedStatus", (status != null && !status.isBlank()) ? status.toUpperCase() : "ALL");
        model.addAttribute("search", search);
        return "admin/contacts";
    }

    @PostMapping("/mark-read/{id}")
    @ResponseBody
    public ResponseEntity<?> markRead(@PathVariable Long id) {
        try {
            contactService.markAsRead(id);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/mark-replied/{id}")
    public String markReplied(@PathVariable Long id, RedirectAttributes ra) {
        try {
            contactService.markAsReplied(id);
            ra.addFlashAttribute("successMessage", "Đã cập nhật trạng thái: Đã phản hồi.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/contacts";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        try {
            contactService.deleteContact(id);
            ra.addFlashAttribute("successMessage", "Đã xóa tin nhắn liên hệ thành công.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/contacts";
    }
}
