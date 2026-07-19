package com.school.system.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class RateLimitingFilter extends OncePerRequestFilter {

    private final StringRedisTemplate redisTemplate;
    
    @Value("${app.security.rate-limit.capacity:100}")
    private int limitCapacity;

    @Value("${app.security.rate-limit.fill-period-seconds:60}")
    private int periodSeconds;

    // Local fallback in case Redis connection is lost
    private final ConcurrentHashMap<String, RequestCounter> fallbackCache = new ConcurrentHashMap<>();

    public RateLimitingFilter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if ("/".equals(path) || "/health".equals(path) || path.startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Rate limit by client IP address
        String clientIp = getClientIp(request);
        String key = "rate:limit:" + clientIp;
        boolean allowed = false;

        try {
            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null) {
                if (count == 1) {
                    redisTemplate.expire(key, periodSeconds, TimeUnit.SECONDS);
                }
                allowed = count <= limitCapacity;
            }
        } catch (Exception e) {
            log.warn("Redis rate limiter unavailable: {}. Falling back to in-memory rate limiting.", e.getMessage());
            allowed = checkFallbackRateLimit(clientIp);
        }

        if (!allowed) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"status\": 429, \"error\": \"Too Many Requests\", \"message\": \"API rate limit exceeded. Please try again later.\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean checkFallbackRateLimit(String ip) {
        long now = System.currentTimeMillis();
        RequestCounter counter = fallbackCache.compute(ip, (k, v) -> {
            if (v == null || now - v.resetTime > (periodSeconds * 1000L)) {
                return new RequestCounter(new AtomicInteger(1), now);
            }
            v.count.incrementAndGet();
            return v;
        });

        return counter.count.get() <= limitCapacity;
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    private static class RequestCounter {
        final AtomicInteger count;
        final long resetTime;

        RequestCounter(AtomicInteger count, long resetTime) {
            this.count = count;
            this.resetTime = resetTime;
        }
    }
}
