package com.jobautomation.service;

import com.jobautomation.exception.RateLimitExceededException;
import com.jobautomation.model.User;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitingService {

    private final StringRedisTemplate redisTemplate;
    private final ConcurrentHashMap<String, AtomicInteger> inMemoryHourlyCounters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> inMemoryDailyCounters = new ConcurrentHashMap<>();

    public RateLimitingService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void checkAndIncrementRateLimit(User user) {
        String hourlyKey = "rate:user:" + user.getId() + ":hourly";
        String dailyKey = "rate:user:" + user.getId() + ":daily";

        try {
            Long hourlyCount = redisTemplate.opsForValue().increment(hourlyKey);
            if (hourlyCount != null && hourlyCount == 1) {
                redisTemplate.expire(hourlyKey, Duration.ofHours(1));
            }

            Long dailyCount = redisTemplate.opsForValue().increment(dailyKey);
            if (dailyCount != null && dailyCount == 1) {
                redisTemplate.expire(dailyKey, Duration.ofDays(1));
            }

            if (hourlyCount != null && hourlyCount > user.getHourlyLimit()) {
                throw new RateLimitExceededException("Hourly email limit reached (" + user.getHourlyLimit() + " emails/hour). Please try again later.");
            }

            if (dailyCount != null && dailyCount > user.getDailyLimit()) {
                throw new RateLimitExceededException("Daily email limit reached (" + user.getDailyLimit() + " emails/day). Please try again tomorrow.");
            }
        } catch (RateLimitExceededException ex) {
            throw ex;
        } catch (Exception ex) {
            // Fallback to in-memory rate limiter if Redis is offline locally
            int hourly = inMemoryHourlyCounters.computeIfAbsent(user.getId(), k -> new AtomicInteger(0)).incrementAndGet();
            int daily = inMemoryDailyCounters.computeIfAbsent(user.getId(), k -> new AtomicInteger(0)).incrementAndGet();

            if (hourly > user.getHourlyLimit()) {
                throw new RateLimitExceededException("Hourly limit reached (" + user.getHourlyLimit() + " emails/hour).");
            }
            if (daily > user.getDailyLimit()) {
                throw new RateLimitExceededException("Daily limit reached (" + user.getDailyLimit() + " emails/day).");
            }
        }
    }

    public boolean acquireSendLock(String applicationId) {
        String lockKey = "lock:application:" + applicationId;
        try {
            Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", Duration.ofMinutes(5));
            return Boolean.TRUE.equals(success);
        } catch (Exception e) {
            return true; // Fallback allow if lock check passes
        }
    }

    public void releaseSendLock(String applicationId) {
        String lockKey = "lock:application:" + applicationId;
        try {
            redisTemplate.delete(lockKey);
        } catch (Exception ignored) {}
    }
}
