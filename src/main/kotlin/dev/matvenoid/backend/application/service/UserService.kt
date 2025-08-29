package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.application.dto.UpdateEmailRequest
import dev.matvenoid.backend.application.dto.UpdateNameRequest
import dev.matvenoid.backend.application.dto.UpdatePasswordRequest
import dev.matvenoid.backend.application.dto.UpdateUsernameRequest
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
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
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
    private val passwordEncoder: PasswordEncoder,
) : UserUseCase {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    @Transactional(readOnly = true)
    override fun findById(id: UUID): UserResponse =
        findUserOrThrow(id).toResponse()

    @Transactional(readOnly = true)
    override fun findByUsername(username: String): UserResponse =
        findUserOrThrow(username).toResponse()


    @Transactional
    override fun updateEmail(id: UUID, request: UpdateEmailRequest) {
        val user = findUserOrThrow(id)

        val updatedUser = user.copy(
            pendingEmail = request.email,
            pendingEmailRequestedAt = OffsetDateTime.now(ZoneOffset.UTC),
        )

        try {
            userRepository.save(updatedUser)
        } catch (_: DataIntegrityViolationException) {
            logger.warn("Update failed: user already exists ({})", request.email)
            throw UserAlreadyExistsException("Адрес уже используется")
        }

        val code = verificationCodeGenerator.generateCode()
        emailVerificationService.saveVerificationCode(CHANGE, request.email, code)
        emailService.sendVerificationEmail(request.email, code)

        logger.info("User email updated: awaiting confirmation ({})", user.email)
    }

    @Transactional
    override fun updateName(id: UUID, request: UpdateNameRequest): UserResponse {
        val user = findUserOrThrow(id)
        val updatedUser = userRepository.save(user.copy(name = request.name))

        logger.info("User name updated ({})", user.email)
        return updatedUser.toResponse()
    }

    @Transactional
    override fun updateUsername(id: UUID, request: UpdateUsernameRequest): UserResponse {
        val user = findUserOrThrow(id)
        if (userRepository.findByUsername(request.username) != null) {
            logger.warn("Update failed: username already taken ({})", request.username)
            throw UserAlreadyExistsException("Имя пользователя уже используется")
        }

        val updatedUser = userRepository.save(user.copy(username = request.username))

        logger.info("Username updated ({})", user.email)
        return updatedUser.toResponse()
    }

    override fun updatePassword(id: UUID, request: UpdatePasswordRequest): UserResponse {
        val user = findUserOrThrow(id)

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            logger.warn("Update password failed: bad credentials ({})", id)
            throw BadCredentialsException("Неверный пароль")
        }

        val updatedUser = userRepository.save(
            user.copy(passwordHash = passwordEncoder.encode(request.newPassword))
        )

        logger.info("User password updated ({})", user.email)
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
