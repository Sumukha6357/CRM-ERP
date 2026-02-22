package com.gully.ggos.service;

import com.gully.ggos.api.error.RateLimitException;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class LoginRateLimiter {
    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_SECONDS = 10 * 60;

    private final Map<String, Deque<Long>> attempts = new ConcurrentHashMap<>();

    public void checkRateLimit(String key) {
        long now = Instant.now().getEpochSecond();
        Deque<Long> deque = attempts.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (deque) {
            while (!deque.isEmpty() && now - deque.peekFirst() > WINDOW_SECONDS) {
                deque.removeFirst();
            }
            if (deque.size() >= MAX_ATTEMPTS) {
                throw new RateLimitException("Too many login attempts");
            }
            deque.addLast(now);
        }
    }
}
