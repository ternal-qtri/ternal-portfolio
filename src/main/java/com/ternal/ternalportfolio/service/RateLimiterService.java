package com.ternal.ternalportfolio.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class RateLimiterService {

    private final Map<String, Deque<Long>> requestLog = new ConcurrentHashMap<>();

    // Mặc định: tối đa 3 yêu cầu trong 60 giây cho mỗi địa chỉ IP
    private static final int DEFAULT_MAX_REQUESTS = 3;
    private static final long DEFAULT_WINDOW_MILLIS = 60_000L;

    public boolean isAllowed(String ip) {
        return isAllowed(ip, DEFAULT_MAX_REQUESTS, DEFAULT_WINDOW_MILLIS);
    }

    public synchronized boolean isAllowed(String ip, int maxRequests, long windowMillis) {
        if (ip == null || ip.isBlank()) {
            return true;
        }

        long now = System.currentTimeMillis();
        long threshold = now - windowMillis;

        // Định kỳ dọn dẹp nếu kích thước map lớn hơn 1000
        if (requestLog.size() > 1000) {
            requestLog.entrySet().removeIf(entry -> entry.getValue().isEmpty() || entry.getValue().peekLast() < threshold);
        }

        Deque<Long> timestamps = requestLog.computeIfAbsent(ip, k -> new ArrayDeque<>());

        // Loại bỏ các mốc thời gian ngoài cửa sổ trượt
        while (!timestamps.isEmpty() && timestamps.peekFirst() < threshold) {
            timestamps.pollFirst();
        }

        if (timestamps.size() >= maxRequests) {
            log.warn("Rate limit vượt ngưỡng cho IP: {} ({} yêu cầu trong {}ms)", ip, timestamps.size(), windowMillis);
            return false;
        }

        timestamps.addLast(now);
        return true;
    }

    public String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            String[] parts = xff.split(",");
            if (parts.length > 0 && !parts[0].isBlank()) {
                return parts[0].trim();
            }
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }

        return request.getRemoteAddr();
    }
}
