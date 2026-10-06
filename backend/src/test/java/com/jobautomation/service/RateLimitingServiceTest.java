package com.jobautomation.service;

import com.jobautomation.exception.RateLimitExceededException;
import com.jobautomation.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RateLimitingServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private RateLimitingService rateLimitingService;
    private User user;

    @BeforeEach
    void setUp() {
        rateLimitingService = new RateLimitingService(redisTemplate);
        user = new User();
        user.setId("user123");
        user.setHourlyLimit(2);
        user.setDailyLimit(5);
    }

    @Test
    void testRateLimit_PassUnderLimit() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(1L);

        assertDoesNotThrow(() -> rateLimitingService.checkAndIncrementRateLimit(user));
    }

    @Test
    void testRateLimit_ExceedHourlyLimit() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.increment(anyString())).thenReturn(3L);

        assertThrows(RateLimitExceededException.class, () -> rateLimitingService.checkAndIncrementRateLimit(user));
    }
}
