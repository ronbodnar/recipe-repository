package com.ronbodnar.recipes.modules.auth;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PasswordResetService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String TOKEN_PREFIX = "password:reset:token:";

    private static final long EXPIRATION_SECONDS = 60 * 15;

    public PasswordResetService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String createResetToken(String email) {
        String token = UUID.randomUUID().toString();
        String redisKey = TOKEN_PREFIX + token;

        redisTemplate.opsForValue().set(redisKey, email, Expiration.seconds(EXPIRATION_SECONDS));

        return token;
    }

    public String validateTokenAndGetEmail(String token) {
        return redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
    }

    public void invalidateToken(String token) {
        redisTemplate.delete(TOKEN_PREFIX + token);
    }
}
