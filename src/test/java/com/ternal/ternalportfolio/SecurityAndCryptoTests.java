package com.ternal.ternalportfolio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ternal.ternalportfolio.controller.ContactController;
import com.ternal.ternalportfolio.controller.CustomErrorPageController;
import com.ternal.ternalportfolio.service.CryptoService;
import com.ternal.ternalportfolio.service.RateLimiterService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityAndCryptoTests {

    @Autowired
    private CryptoService cryptoService;

    @Autowired
    private RateLimiterService rateLimiterService;

    @Autowired
    private ContactController contactController;

    @Autowired
    private CustomErrorPageController customErrorPageController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Kiểm tra khởi tạo Public Key RSA của CryptoService")
    void testCryptoServicePublicKey() {
        String pubKey = cryptoService.getPublicKeyBase64();
        assertNotNull(pubKey, "Public Key không được null");
        assertFalse(pubKey.isBlank(), "Public Key không được rỗng");
    }

    @Test
    @DisplayName("Kiểm tra mã hóa Client và giải mã Server (RSA-OAEP + AES-GCM)")
    void testHybridEncryptionDecryption() throws Exception {
        String pubKeyB64 = cryptoService.getPublicKeyBase64();
        byte[] pubKeyBytes = Base64.getDecoder().decode(pubKeyB64);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey serverPubKey = keyFactory.generatePublic(keySpec);

        // Client: Sinh khóa AES-256
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(256);
        SecretKey aesKey = kg.generateKey();
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);

        // Client: Mã hóa Payload JSON bằng AES-GCM
        Map<String, Object> payload = Map.of(
                "fullName", "Nguyen Van A",
                "email", "nguyenvana@gmail.com",
                "subject", "Tư vấn kiến trúc Backend",
                "message", "Xin chào, tôi muốn trao đổi về dự án mới.",
                "timestamp", System.currentTimeMillis()
        );
        String jsonPayload = objectMapper.writeValueAsString(payload);

        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(128, iv));
        byte[] encryptedDataBytes = aesCipher.doFinal(jsonPayload.getBytes(StandardCharsets.UTF_8));

        // Client: Mã hóa khóa AES bằng RSA-OAEP (SHA-256 / MGF1 SHA-256)
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
        rsaCipher.init(Cipher.ENCRYPT_MODE, serverPubKey, oaepParams);
        byte[] encryptedKeyBytes = rsaCipher.doFinal(aesKey.getEncoded());

        String encryptedDataB64 = Base64.getEncoder().encodeToString(encryptedDataBytes);
        String encryptedKeyB64 = Base64.getEncoder().encodeToString(encryptedKeyBytes);
        String ivB64 = Base64.getEncoder().encodeToString(iv);

        // Server: Giải mã
        String decryptedJson = cryptoService.decrypt(encryptedDataB64, encryptedKeyB64, ivB64);
        assertEquals(jsonPayload, decryptedJson, "Dữ liệu sau khi giải mã phải trùng khớp với payload gốc");
    }

    @Test
    @DisplayName("Kiểm tra Rate Limiting giới hạn tần suất yêu cầu")
    void testRateLimiting() {
        String testIp = "192.168.99.100";
        // 3 yêu cầu đầu tiên được phép
        assertTrue(rateLimiterService.isAllowed(testIp));
        assertTrue(rateLimiterService.isAllowed(testIp));
        assertTrue(rateLimiterService.isAllowed(testIp));
        // Yêu cầu thứ 4 bị chặn
        assertFalse(rateLimiterService.isAllowed(testIp));

        // IP khác vẫn được phép bình thường
        assertTrue(rateLimiterService.isAllowed("192.168.99.101"));
    }

    @Test
    @DisplayName("Kiểm tra API lấy Public Key phục vụ mã hóa")
    void testGetPublicKeyEndpoint() {
        Map<String, String> response = contactController.getPublicKey();
        assertNotNull(response);
        assertTrue(response.containsKey("publicKey"));
        assertNotNull(response.get("publicKey"));
    }

    @Test
    @DisplayName("Kiểm tra Controller định tuyến các trang lỗi 401, 403, 404, 500")
    void testErrorPageRouting() {
        assertEquals("error/401", customErrorPageController.error401());
        assertEquals("error/403", customErrorPageController.error403());
        assertEquals("error/404", customErrorPageController.error404());
        assertEquals("error/500", customErrorPageController.error500());
    }

    @Test
    @DisplayName("Kiểm tra nạp và thay thế placeholder template email contact-success và admin notification")
    void testEmailTemplateRendering() throws Exception {
        org.springframework.core.io.ClassPathResource successResource =
                new org.springframework.core.io.ClassPathResource("templates/client/email/contact-success.html");
        assertTrue(successResource.exists(), "File contact-success.html phải tồn tại trong classpath");
        String successTemplate = new String(successResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(successTemplate.contains("{{name}}"));
        assertTrue(successTemplate.contains("{{email}}"));
        assertTrue(successTemplate.contains("{{subject}}"));
        assertTrue(successTemplate.contains("{{submittedAt}}"));
        assertTrue(successTemplate.contains("{{message}}"));
        assertTrue(successTemplate.contains("{{portfolioUrl}}"));
        assertTrue(successTemplate.contains("#09090B"), "Phải chứa màu nền đen chuẩn design-system");
        assertTrue(successTemplate.contains("#121214"), "Phải chứa màu nền bento surface chuẩn design-system");

        org.springframework.core.io.ClassPathResource adminResource =
                new org.springframework.core.io.ClassPathResource("templates/client/email/contact-admin-notification.html");
        assertTrue(adminResource.exists(), "File contact-admin-notification.html phải tồn tại trong classpath");
        String adminTemplate = new String(adminResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(adminTemplate.contains("{{name}}"));
        assertTrue(adminTemplate.contains("{{email}}"));
        assertTrue(adminTemplate.contains("{{subject}}"));
        assertTrue(adminTemplate.contains("{{submittedAt}}"));
        assertTrue(adminTemplate.contains("{{message}}"));
        assertTrue(adminTemplate.contains("{{adminUrl}}"));
        assertTrue(adminTemplate.contains("{{replyUrl}}"));
    }
}
