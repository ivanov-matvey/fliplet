package dev.matvenoid.backend.application.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataAccessException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class   EmailVerificationService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    private val logger = LoggerFactory.getLogger(EmailVerificationService::class.java)

    @Value($$"${email.verification-code-expiration-minutes}")
    private var verificationCodeExpirationMinutes: Long = 15

    private val keyPrefix = "email_verification:"

    fun saveVerificationCode(email: String, code: String) {
        val key = keyPrefix + email.lowercase()
        try {
            logger.info("Saving verification code for email: {}", key)
            redisTemplate.opsForValue().set(key, code, Duration.ofMinutes(verificationCodeExpirationMinutes))
        } catch (e: DataAccessException) {
            logger.error("Failed to save verification code for key {}: Redis error.", key, e)
            throw e
        }
    }

    fun getVerificationCode(email: String): String? {
        val key = keyPrefix + email.lowercase()
        return try {
            logger.debug("Attempting to get verification code for key: {}", key)
            redisTemplate.opsForValue().get(key)
        } catch (e: DataAccessException) {
            logger.error("Failed to get verification code for key {}: Redis error.", key, e)
            throw e
        }
    }

    fun deleteVerificationCode(email: String) {
        val key = keyPrefix + email.lowercase()
        try {
            logger.info("Deleting verification code for key: {}", key)
            redisTemplate.delete(key)
        } catch (e: DataAccessException) {
            logger.error("Failed to delete verification code for key {}: Redis error.", key, e)
            throw e
        }
    }

    fun isCodeValid(email: String, code: String): Boolean {
        val storedCode = getVerificationCode(email)
        val isValid = storedCode != null && storedCode == code
        logger.debug(
            "Code validation for email {}: provided code='{}', stored code='{}', result={}",
            email, code, storedCode ?: "null", isValid
        )
        return isValid
    }
}
