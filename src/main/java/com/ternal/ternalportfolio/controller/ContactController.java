package com.ternal.ternalportfolio.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ternal.ternalportfolio.service.ContactService;
import com.ternal.ternalportfolio.service.CryptoService;
import com.ternal.ternalportfolio.service.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/contact")
@RequiredArgsConstructor
@Slf4j
public class ContactController {

    private final ContactService contactService;
    private final CryptoService cryptoService;
    private final RateLimiterService rateLimiterService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping
    public String contact(Model model) {
        model.addAttribute("publicKey", cryptoService.getPublicKeyBase64());
        return "client/contact";
    }

    @GetMapping("/pubkey")
    @ResponseBody
    public Map<String, String> getPublicKey() {
        return Map.of("publicKey", cryptoService.getPublicKeyBase64());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<?> submitContact(
            HttpServletRequest request,
            @RequestParam(required = false) String encryptedData,
            @RequestParam(required = false) String encryptedKey,
            @RequestParam(required = false) String iv,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String message,
            @RequestParam(required = false, name = "_hp_website") String hpWebsite) {

        // 1. Kiểm tra Rate Limiting theo Client IP (Tối đa 3 yêu cầu / 60 giây)
        String clientIp = rateLimiterService.getClientIp(request);
        if (!rateLimiterService.isAllowed(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of(
                    "success", false,
                    "message", "Bạn đang gửi yêu cầu quá nhanh. Vui lòng thử lại sau 1 phút!"
            ));
        }

        // 2. Kiểm tra Honeypot gửi dạng plain (nếu bot điền trường ẩn này)
        if (hpWebsite != null && !hpWebsite.trim().isEmpty()) {
            log.warn("Phát hiện Spambot qua Honeypot từ IP: {}", clientIp);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cảm ơn bạn! Tin nhắn đã được gửi thành công đến Nguyễn Quốc Trí."
            ));
        }

        String finalName = fullName;
        String finalEmail = email;
        String finalSubject = subject;
        String finalMessage = message;

        // 3. Xử lý giải mã Payload nếu có dữ liệu mã hóa từ Client
        if (encryptedData != null && !encryptedData.isBlank()
                && encryptedKey != null && !encryptedKey.isBlank()
                && iv != null && !iv.isBlank()) {
            try {
                String decryptedJson = cryptoService.decrypt(encryptedData, encryptedKey, iv);
                JsonNode node = objectMapper.readTree(decryptedJson);

                // Kiểm tra Honeypot bên trong gói mã hóa
                JsonNode hpNode = node.get("_hp_website");
                if (hpNode != null && !hpNode.asText("").trim().isEmpty()) {
                    log.warn("Phát hiện Spambot qua encrypted Honeypot từ IP: {}", clientIp);
                    return ResponseEntity.ok(Map.of(
                            "success", true,
                            "message", "Cảm ơn bạn! Tin nhắn đã được gửi thành công đến Nguyễn Quốc Trí."
                    ));
                }

                // Chống tấn công Replay Attack: kiểm tra lệch thời gian không quá 15 phút
                JsonNode tsNode = node.get("timestamp");
                if (tsNode != null && tsNode.isNumber()) {
                    long clientTimestamp = tsNode.asLong();
                    long serverTimestamp = System.currentTimeMillis();
                    if (Math.abs(serverTimestamp - clientTimestamp) > 15 * 60 * 1000L) {
                        return ResponseEntity.badRequest().body(Map.of(
                                "success", false,
                                "message", "Phiên gửi tin nhắn đã quá hạn (Timestamp invalid). Vui lòng gửi lại!"
                        ));
                    }
                }

                finalName = node.has("fullName") ? node.get("fullName").asText("") : "";
                finalEmail = node.has("email") ? node.get("email").asText("") : "";
                finalSubject = node.has("subject") ? node.get("subject").asText("") : "";
                finalMessage = node.has("message") ? node.get("message").asText("") : "";
                log.info("Giải mã dữ liệu liên hệ đầu cuối thành công từ client IP: {}", clientIp);
            } catch (Exception e) {
                log.error("Không thể giải mã dữ liệu liên hệ từ IP {}: {}", clientIp, e.getMessage());
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Dữ liệu mã hóa không hợp lệ hoặc đã bị can thiệp."
                ));
            }
        }

        // 4. Lưu CSDL và gửi email bất đồng bộ
        try {
            contactService.processContact(finalName, finalEmail, finalSubject, finalMessage);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cảm ơn bạn! Tin nhắn đã được gửi thành công đến Nguyễn Quốc Trí."
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Lỗi khi xử lý lưu liên hệ từ {}: {}", clientIp, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Đã có lỗi xảy ra khi gửi tin nhắn. Vui lòng thử lại sau!"
            ));
        }
    }
}
