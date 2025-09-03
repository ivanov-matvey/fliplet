package dev.matvenoid.backend.infrastructure.messaging.redis

import dev.matvenoid.backend.domain.repository.UserRepository
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Component
class RedisExpiryListener(
    private val userRepository: UserRepository
) : MessageListener {
    override fun onMessage(message: Message, pattern: ByteArray?) {
        val key = message.body.decodeToString()

        if (!key.startsWith("email_verification:change:")) return

        val email = key.substringAfterLast(':')
        userRepository.findByPendingEmail(email)?.let { user ->
            userRepository.save(
                user.copy(
                    pendingEmail = null,
                    pendingEmailRequestedAt = null,
                    updatedAt = OffsetDateTime.now(ZoneOffset.UTC)
                )
            )
        }
    }
}
