package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.application.dto.*
import dev.matvenoid.backend.application.usecase.AuthUseCase
import dev.matvenoid.backend.application.util.UsernameGenerator
import dev.matvenoid.backend.application.service.VerificationType.*
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
import kotlin.random.Random

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val tokenBlacklistService: TokenBlacklistService,
    private val usernameGenerator: UsernameGenerator,
    private val emailVerificationService: EmailVerificationService,
    private val emailService: EmailService,
) : AuthUseCase {
    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    @Transactional
    override fun register(request: RegistrationRequest) {
        logger.info("Attempting to register new user with email: {}", request.email)

        if (userRepository.existsByEmail(request.email)) {
            logger.warn("Registration failed for email {}: user already exists.", request.email)
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
        logger.info("User {} (ID: {}) successfully saved to database.", savedUser.email, savedUser.id)

        val code = generateVerificationCode()
        emailVerificationService.saveVerificationCode(REGISTER, savedUser.email, code)
        emailService.sendVerificationEmail(savedUser.email, code)

        logger.info("Successfully registered user {}. Verification email dispatch initiated.", savedUser.email)
    }

    @Transactional
    override fun verifyEmail(request: VerifyRequest) {
        logger.info("Attempting to verify email: {}", request.email)

        if (emailVerificationService.isCodeValid(REGISTER, request.email, request.code)) {

            val user = findUnverifiedUserByEmailOrThrow(request.email)
            userRepository.save(user.copy(isEmailVerified = true))
            emailVerificationService.deleteVerificationCode(REGISTER, request.email)

            logger.info("Registration email verified for {}", request.email)
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

            logger.info("Email successfully changed for user {}", user.id)
            return
        }

        logger.warn("Invalid verification code for email: {}", request.email)
        throw BadCredentialsException("Неверный или истекший код подтверждения")
    }

    @Transactional(readOnly = true)
    override fun resendVerificationCode(request: ResendVerificationCodeRequest) {
        logger.info("Resending verification code for email: {}", request.email)

        val email = request.email.lowercase()

        val userByEmail = userRepository.findByEmail(email)
        val userByPending = userRepository.findByPendingEmail(email)

        when {
            userByEmail?.isEmailVerified == true ->
                throw IllegalStateException("E-mail уже подтверждён")

            userByPending != null -> {
                if (userByEmail != null) {
                    logger.warn("Registration failed for email {}: user already exists.", request.email)
                    throw UserAlreadyExistsException("Неверный адрес электронной почты")
                }

                val code = generateVerificationCode()
                emailVerificationService.saveVerificationCode(CHANGE, email, code)
                emailService.sendVerificationEmail(email, code)
            }

            userByEmail != null -> {
                val code = generateVerificationCode()
                emailVerificationService.saveVerificationCode(REGISTER, email, code)
                emailService.sendVerificationEmail(email, code)
            }

            else -> throw UserNotFoundException("Пользователь не найден")
        }

        logger.info("Verification code successfully re-sent for email: {}", request.email)
    }

    override fun login(request: LoginRequest): AuthResponse {
        logger.info("Login attempt for email: {}", request.email)

        val user = userRepository.findByEmail(request.email)?: run {
            logger.warn("Failed login attempt for email {}: user not found.", request.email)
            throw BadCredentialsException("Неверный адрес электронной почты или пароль")
        }

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            logger.warn("Failed login attempt for email {}: bad credentials.", request.email)
            throw BadCredentialsException("Неверный адрес электронной почты или пароль")
        }

        if (!user.isEmailVerified) {
            logger.warn("Failed login attempt for email {}: email not verified.", request.email)
            throw BadCredentialsException("Электронная почта не подтверждена")
        }

        logger.info("User {} logged in successfully.", request.email)
        val accessToken = jwtService.generateAccessToken(user.id)
        val refreshToken = jwtService.generateRefreshToken(user.id)
        return AuthResponse(accessToken, refreshToken)
    }

    override fun logout(request: RefreshTokenRequest) {
        val userId = jwtService.extractUserId(request.refreshToken)
        if (userId != null) {
            logger.info("User with ID {} is logging out.", userId)
        } else logger.warn("Logout attempt with a token from which user ID could not be extracted.")

        val remainingTime = jwtService.getRemainingExpiration(request.refreshToken)
        tokenBlacklistService.addToBlacklist(request.refreshToken, remainingTime)
        logger.info("Refresh token for user ID {} has been blacklisted.", userId)
    }

    override fun refresh(request: RefreshTokenRequest): AuthResponse {
        logger.info("Attempting to refresh tokens...")

        if (tokenBlacklistService.isBlacklisted(request.refreshToken)) {
            logger.warn("Token refresh attempt failed: refresh token is blacklisted.")
            throw JwtException("Токен находится в черном списке")
        }

        val userId = jwtService.extractUserId(request.refreshToken) ?: run {
            logger.warn("Token refresh failed: could not extract user ID from refresh token.")
            throw JwtException("Не удалось извлечь ID пользователя")
        }

        val user = userRepository.findById(userId) ?: run {
            logger.warn("Token refresh failed for user ID {}: user not found.", userId)
            throw JwtException("Пользователь не найден")
        }

        if (!user.isEmailVerified) {
            logger.warn("Token refresh failed for email {}: email not verified.", user.email)
            throw JwtException("Электронная почта не подтверждена")
        }

        val remainingTime = jwtService.getRemainingExpiration(request.refreshToken)
        tokenBlacklistService.addToBlacklist(request.refreshToken, remainingTime)

        logger.info("Successfully refreshed tokens for user {}.", user.email)
        val newAccessToken = jwtService.generateAccessToken(user.id)
        val newRefreshToken = jwtService.generateRefreshToken(user.id)
        return AuthResponse(newAccessToken, newRefreshToken)
    }

    private fun findUserByEmailOrThrow(email: String): User {
        logger.debug("Finding user by email: {}", email)
        return userRepository.findByEmail(email) ?: run {
            logger.warn("Operation failed for email {}: user not found.", email)
            throw UserNotFoundException("Неверный адрес электронной почты")
        }
    }

    private fun findUnverifiedUserByEmailOrThrow(email: String): User {
        val user = findUserByEmailOrThrow(email)
        if (user.isEmailVerified) {
            logger.warn("Operation failed for {}: email is already verified.", email)
            throw IllegalStateException("Электронная почта уже подтверждена")
        }
        return user
    }

    private fun generateVerificationCode(): String =
        Random.nextInt(100_000, 999_999).toString()
}
