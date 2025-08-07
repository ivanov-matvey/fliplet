package dev.matvenoid.backend.application.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class TokenBlacklistService(
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private val keyPrefix = "blacklist:"

    fun addToBlacklist(token: String, expiration: Duration) {
        val key = keyPrefix + token
        redisTemplate.opsForValue().set(key, "1", expiration)
    }

    fun isBlacklisted(token: String): Boolean {
        val key = keyPrefix + token
        return redisTemplate.hasKey(key)
    }
}
