package com.ternal.ternalportfolio.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;

@Service
@Slf4j
public class CryptoService {

    private KeyPair rsaKeyPair;
    private String publicKeyBase64;

    @PostConstruct
    public void init() {
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            this.rsaKeyPair = kpg.generateKeyPair();
            this.publicKeyBase64 = Base64.getEncoder().encodeToString(this.rsaKeyPair.getPublic().getEncoded());
            log.info("Khởi tạo thành công cặp khóa RSA-2048 cho mã hóa dữ liệu đầu cuối phía Client.");
        } catch (NoSuchAlgorithmException e) {
            log.error("Không thể khởi tạo cặp khóa RSA: {}", e.getMessage(), e);
            throw new IllegalStateException("RSA algorithm not available", e);
        }
    }

    public String getPublicKeyBase64() {
        return publicKeyBase64;
    }

    public String decrypt(String encryptedDataB64, String encryptedKeyB64, String ivB64) throws GeneralSecurityException {
        if (encryptedDataB64 == null || encryptedKeyB64 == null || ivB64 == null) {
            throw new IllegalArgumentException("Dữ liệu mã hóa không đầy đủ.");
        }

        byte[] encryptedKeyBytes = Base64.getDecoder().decode(encryptedKeyB64);
        byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedDataB64);
        byte[] ivBytes = Base64.getDecoder().decode(ivB64);

        // 1. Giải mã khóa AES phiên bằng RSA-OAEP (SHA-256 / MGF1 SHA-256)
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        OAEPParameterSpec oaepParams = new OAEPParameterSpec(
                "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
        rsaCipher.init(Cipher.DECRYPT_MODE, rsaKeyPair.getPrivate(), oaepParams);
        byte[] rawAesKey = rsaCipher.doFinal(encryptedKeyBytes);

        // 2. Giải mã Payload bằng AES-256-GCM
        SecretKey aesKey = new SecretKeySpec(rawAesKey, "AES");
        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        aesCipher.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(128, ivBytes));
        byte[] decryptedBytes = aesCipher.doFinal(encryptedDataBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
