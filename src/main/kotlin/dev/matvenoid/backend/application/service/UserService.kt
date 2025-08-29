package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.application.dto.UserResponse
import dev.matvenoid.backend.application.mapper.toResponse
import dev.matvenoid.backend.application.usecase.UserUseCase
import dev.matvenoid.backend.application.service.VerificationType.CHANGE
import dev.matvenoid.backend.application.util.VerificationCodeGenerator
import dev.matvenoid.backend.domain.exception.UserAlreadyExistsException
import dev.matvenoid.backend.domain.exception.UserNotFoundException
import dev.matvenoid.backend.domain.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val emailVerificationService: EmailVerificationService,
    private val emailService: EmailService,
    private val verificationCodeGenerator: VerificationCodeGenerator,
) : UserUseCase {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    @Transactional(readOnly = true)
    override fun findById(id: UUID): UserResponse =
        findUserOrThrow(id).toResponse()

    @Transactional(readOnly = true)
    override fun findByUsername(username: String): UserResponse =
        findUserOrThrow(username).toResponse()


    @Transactional
    override fun updateEmail(id: UUID, newEmail: String) {
        val user = findUserOrThrow(id)

        val updatedUser = user.copy(
            pendingEmail = newEmail,
            pendingEmailRequestedAt = OffsetDateTime.now(ZoneOffset.UTC),
        )

        try {
            userRepository.save(updatedUser)
        } catch (_: DataIntegrityViolationException) {
            logger.warn("Update failed: user already exists ({})", newEmail)
            throw UserAlreadyExistsException("Адрес уже используется")
        }

        val code = verificationCodeGenerator.generateCode()
        emailVerificationService.saveVerificationCode(CHANGE, newEmail, code)
        emailService.sendVerificationEmail(newEmail, code)

        logger.info("Email updated: awaiting confirmation ({})", newEmail)
    }

    @Transactional
    override fun updateName(id: UUID, newName: String): UserResponse {
        val user = findUserOrThrow(id)
        val updatedUser = userRepository.save(user.copy(name = newName))

        logger.info("User updated ({})", newName)
        return updatedUser.toResponse()
    }

    @Transactional
    override fun updateUsername(id: UUID, newUsername: String): UserResponse {
        val user = findUserOrThrow(id)
        if (userRepository.findByUsername(newUsername) != null) {
            logger.warn("Update failed: username already taken ({})", newUsername)
            throw UserAlreadyExistsException("Имя пользователя уже используется")
        }

        val updatedUser = userRepository.save(user.copy(username = newUsername))

        logger.info("User updated ({})", newUsername)
        return updatedUser.toResponse()
    }

    private fun findUserOrThrow(id: UUID) =
        userRepository.findById(id) ?: run {
            logger.warn("Operation Failed: User not found ({})", id)
            throw UserNotFoundException("Пользователь не найден")
        }

    private fun findUserOrThrow(username: String) =
        userRepository.findByUsername(username) ?: run {
            logger.warn("Operation Failed: User not found ({})", username)
            throw UserNotFoundException("Пользователь $username не найден")
        }
}
