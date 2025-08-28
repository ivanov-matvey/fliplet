package dev.matvenoid.backend.application.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.DataAccessException
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

enum class VerificationType { REGISTER, CHANGE }

@Service
class EmailVerificationService(
    private val redisTemplate: RedisTemplate<String, String>
) {
    private val logger = LoggerFactory.getLogger(EmailVerificationService::class.java)

    @Value($$"${email.verification-code-expiration-minutes}")
    private var verificationCodeExpirationMinutes: Long = 15

    private fun generateKey(type: VerificationType, email: String) =
        "email_verification:${type.name.lowercase()}:${email.lowercase()}"

    fun saveVerificationCode(type: VerificationType, email: String, code: String) {
        val key = generateKey(type, email)
        try {
            logger.info("Saving verification code for email: {}", key)
            redisTemplate.opsForValue().set(key, code, Duration.ofMinutes(verificationCodeExpirationMinutes))
        } catch (e: DataAccessException) {
            logger.error("Failed to save verification code for key {}: Redis error.", key, e)
            throw e
        }
    }

    fun getVerificationCode(type: VerificationType, email: String): String? {
        val key = generateKey(type, email)
        return try {
            logger.debug("Attempting to get verification code for key: {}", key)
            redisTemplate.opsForValue().get(key)
        } catch (e: DataAccessException) {
            logger.error("Failed to get verification code for key {}: Redis error.", key, e)
            throw e
        }
    }

    fun deleteVerificationCode(type: VerificationType, email: String) {
        val key = generateKey(type, email)
        try {
            logger.info("Deleting verification code for key: {}", key)
            redisTemplate.delete(key)
        } catch (e: DataAccessException) {
            logger.error("Failed to delete verification code for key {}: Redis error.", key, e)
            throw e
        }
    }

    fun isCodeValid(type: VerificationType, email: String, code: String): Boolean {
        val storedCode = getVerificationCode(type, email)
        val isValid = storedCode != null && storedCode == code
        logger.debug(
            "Code validation for email {}: provided code='{}', stored code='{}', result={}",
            email, code, storedCode ?: "null", isValid
        )
        return isValid
    }
}
