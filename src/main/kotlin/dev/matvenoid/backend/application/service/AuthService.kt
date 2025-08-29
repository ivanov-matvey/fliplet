package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.application.dto.AuthResponse
import dev.matvenoid.backend.application.dto.LoginRequest
import dev.matvenoid.backend.application.dto.RefreshTokenRequest
import dev.matvenoid.backend.application.dto.RegistrationRequest
import dev.matvenoid.backend.application.dto.ResendVerificationCodeRequest
import dev.matvenoid.backend.application.dto.VerifyRequest
import dev.matvenoid.backend.application.usecase.AuthUseCase
import dev.matvenoid.backend.application.util.UsernameGenerator
import dev.matvenoid.backend.application.service.VerificationType.CHANGE
import dev.matvenoid.backend.application.service.VerificationType.REGISTER
import dev.matvenoid.backend.application.util.VerificationCodeGenerator
import dev.matvenoid.backend.domain.exception.UserAlreadyExistsException
import dev.matvenoid.backend.domain.exception.UserNotFoundException
import dev.matvenoid.backend.domain.model.User
import dev.matvenoid.backend.domain.repository.UserRepository
import io.jsonwebtoken.JwtException
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val tokenBlacklistService: TokenBlacklistService,
    private val usernameGenerator: UsernameGenerator,
    private val emailVerificationService: EmailVerificationService,
    private val emailService: EmailService,
    private val verificationCodeGenerator: VerificationCodeGenerator,
) : AuthUseCase {
    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    @Transactional
    override fun register(request: RegistrationRequest) {
        if (userRepository.existsByEmail(request.email)) {
            logger.warn("Registration failed: user already exists ({})", request.email)
            throw UserAlreadyExistsException("Неверный адрес электронной почты")
        }

        val username = usernameGenerator.generate(request.email)
        val user = User.create(
            username = username,
            name = null,
            email = request.email,
            avatarUrl = null,
            passwordHash = passwordEncoder.encode(request.password)
        )

        val savedUser = userRepository.save(user)

        val code = verificationCodeGenerator.generateCode()
        emailVerificationService.saveVerificationCode(REGISTER, savedUser.email, code)
        emailService.sendVerificationEmail(savedUser.email, code)

        logger.info("User registered ({})", savedUser.email)
    }

    @Transactional
    override fun verifyEmail(request: VerifyRequest) {
        if (emailVerificationService.isCodeValid(REGISTER, request.email, request.code)) {

            val user = userRepository.findByEmail(request.email) ?: run {
                logger.warn("Verification failed: user not found ({})", request.email)
                throw UserNotFoundException("Неверный адрес электронной почты")
            }

            if (user.isEmailVerified) {
                logger.warn("Verification failed: already verified ({})", request.email)
                throw IllegalStateException("Электронная почта уже подтверждена")
            }

            userRepository.save(user.copy(isEmailVerified = true))
            emailVerificationService.deleteVerificationCode(REGISTER, request.email)

            logger.info("Email verified ({})", request.email)
            return
        }

        if (emailVerificationService.isCodeValid(CHANGE, request.email, request.code)) {

            val user = userRepository.findByPendingEmail(request.email)
                ?: throw BadCredentialsException("Запрос на смену e-mail не найден")

            userRepository.save(
                user.copy(
                    email = user.pendingEmail!!,
                    pendingEmail = null,
                    pendingEmailRequestedAt = null,
                )
            )
            emailVerificationService.deleteVerificationCode(CHANGE, request.email)

            logger.info("Email changed ({})", user.email)
            return
        }

        logger.warn("Verification failed: invalid verification code ({})", request.email)
        throw BadCredentialsException("Неверный или истекший код подтверждения")
    }

    @Transactional(readOnly = true)
    override fun resendVerificationCode(request: ResendVerificationCodeRequest) {
        val email = request.email.lowercase()

        val userByEmail = userRepository.findByEmail(email)
        val userByPending = userRepository.findByPendingEmail(email)

        when {
            userByEmail?.isEmailVerified == true ->
                throw IllegalStateException("Email уже подтверждён")

            userByPending != null -> {
                if (userByEmail != null) {
                    logger.warn("Resend failed: user already exists ({})", request.email)
                    throw UserAlreadyExistsException("Неверный адрес электронной почты")
                }

                val code = verificationCodeGenerator.generateCode()
                emailVerificationService.saveVerificationCode(CHANGE, email, code)
                emailService.sendVerificationEmail(email, code)
            }

            userByEmail != null -> {
                val code = verificationCodeGenerator.generateCode()
                emailVerificationService.saveVerificationCode(REGISTER, email, code)
                emailService.sendVerificationEmail(email, code)
            }

            else -> throw UserNotFoundException("Пользователь не найден")
        }

        logger.info("Verification code resent ({})", request.email)
    }

    override fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)?: run {
            logger.warn("Login failed: user not found ({})", request.email)
            throw BadCredentialsException("Неверный адрес электронной почты")
        }

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            logger.warn("Login failed: bad credentials ({})", request.email)
            throw BadCredentialsException("Неверный пароль")
        }

        if (!user.isEmailVerified) {
            logger.warn("Login failed: email not verified ({})", request.email)
            throw BadCredentialsException("Электронная почта не подтверждена")
        }

        logger.info("User logged in ({})", request.email)
        val accessToken = jwtService.generateAccessToken(user.id)
        val refreshToken = jwtService.generateRefreshToken(user.id)
        return AuthResponse(accessToken, refreshToken)
    }

    override fun logout(request: RefreshTokenRequest) {
        val userId = jwtService.extractUserId(request.refreshToken)
        val remainingTime = jwtService.getRemainingExpiration(request.refreshToken)
        tokenBlacklistService.addToBlacklist(request.refreshToken, remainingTime)

        if (userId != null) {
            logger.info("User logged out ({})", userId)
        } else logger.warn("Logout failed: could not extract userId")
    }

    override fun refresh(request: RefreshTokenRequest): AuthResponse {
        if (tokenBlacklistService.isBlacklisted(request.refreshToken)) {
            logger.warn("Refresh failed: blacklisted token")
            throw JwtException("Токен находится в черном списке")
        }

        val userId = jwtService.extractUserId(request.refreshToken) ?: run {
            logger.warn("Refresh failed: could not extract userId")
            throw JwtException("Не удалось извлечь ID пользователя")
        }

        val user = userRepository.findById(userId) ?: run {
            logger.warn("Refresh failed: user not found ({})", userId)
            throw JwtException("Пользователь не найден")
        }

        if (!user.isEmailVerified) {
            logger.warn("Refresh failed: email not verified ({})", user.email)
            throw JwtException("Электронная почта не подтверждена")
        }

        val remainingTime = jwtService.getRemainingExpiration(request.refreshToken)
        tokenBlacklistService.addToBlacklist(request.refreshToken, remainingTime)

        logger.info("Tokens refreshed ({})", user.email)
        val newAccessToken = jwtService.generateAccessToken(user.id)
        val newRefreshToken = jwtService.generateRefreshToken(user.id)
        return AuthResponse(newAccessToken, newRefreshToken)
    }
}
