package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.application.dto.UserResponse
import dev.matvenoid.backend.application.usecase.UserUseCase
import dev.matvenoid.backend.application.service.VerificationType.*
import dev.matvenoid.backend.domain.exception.UserAlreadyExistsException
import dev.matvenoid.backend.domain.exception.UserNotFoundException
import dev.matvenoid.backend.domain.repository.UserRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.random.Random

@Service
class UserService(
    private val userRepository: UserRepository,
    private val emailVerificationService: EmailVerificationService,
    private val emailService: EmailService,
) : UserUseCase {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    override fun findById(id: UUID): UserResponse? {
        logger.info("Fetching user by ID: {}", id)

        val user = userRepository.findById(id) ?: run {
            logger.warn("User not found by ID: {}", id)
            throw UserNotFoundException("Пользователь не найден")
        }

        logger.info("User {} found, preparing response", id)
        return UserResponse(
            id = user.id,
            username = user.username,
            name = user.name,
            email = user.email,
            avatarUrl = user.avatarUrl,
        )
    }

    override fun findByUsername(username: String): UserResponse? {
        logger.info("Fetching user by username: {}", username)

        val user = userRepository.findByUsername(username) ?: run {
            logger.warn("User not found by username: {}", username)
            throw UserNotFoundException("Пользователь $username не найден")
        }

        logger.info("User {} found, preparing response", username)
        return UserResponse(
            id = user.id,
            username = user.username,
            name = user.name,
            email = user.email,
            avatarUrl = user.avatarUrl,
        )
    }

    @Transactional
    override fun updateEmail(id: UUID, newEmail: String) {
        logger.info("Updating user by ID: {}", id)

        if (userRepository.existsByEmail(newEmail)) {
            logger.warn("Updating failed for email {}: user already exists.", newEmail)
            throw UserAlreadyExistsException("Адрес уже используется")
        }

        val user = userRepository.findById(id) ?: run {
            logger.warn("User not found by ID: {}", id)
            throw UserNotFoundException("Пользователь не найден")
        }

        val updatedUser = user.copy(
            pendingEmail = newEmail,
            pendingEmailRequestedAt = OffsetDateTime.now(ZoneOffset.UTC),
        )
        userRepository.save(updatedUser)

        val code = Random.nextInt(100_000, 999_999).toString()
        emailVerificationService.saveVerificationCode(CHANGE, newEmail, code)
        emailService.sendVerificationEmail(newEmail, code)

        logger.info("User {} is ready to update: {}", updatedUser.email, newEmail)
    }
}
