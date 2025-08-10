package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.application.dto.AuthResponse
import dev.matvenoid.backend.application.dto.LoginRequest
import dev.matvenoid.backend.application.dto.RefreshTokenRequest
import dev.matvenoid.backend.application.dto.RegistrationRequest
import dev.matvenoid.backend.application.security.UserPrincipal
import dev.matvenoid.backend.application.usecase.AuthUseCase
import dev.matvenoid.backend.application.util.UsernameGenerator
import dev.matvenoid.backend.application.util.normalizePhone
import dev.matvenoid.backend.domain.exception.InvalidTokenException
import dev.matvenoid.backend.domain.exception.UserAlreadyExistsException
import dev.matvenoid.backend.domain.model.User
import dev.matvenoid.backend.domain.repository.UserRepository
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
    private val userDetailsService: UserDetailsService,
    private val authenticationManager: AuthenticationManager,
    private val tokenBlacklistService: TokenBlacklistService,
    private val usernameGenerator: UsernameGenerator,
) : AuthUseCase {


    override fun register(request: RegistrationRequest): AuthResponse {
        val phone = request.phone.normalizePhone()

        if (userRepository.existsByPhone(phone.forDb)) {
            throw UserAlreadyExistsException("Пользователь с телефоном ${phone.forDisplay} уже существует")
        }

        val username = usernameGenerator.generate("user")

        val user = User.create(
            username = username,
            name = "Пользователь",
            phone = phone.forDb,
            avatarUrl = null,
            passwordHash = passwordEncoder.encode(request.password)
        )

        val savedUser = userRepository.save(user)

        val userDetails = userDetailsService.loadUserByUsername(savedUser.phone)
        val principal = userDetails as UserPrincipal

        val accessToken = jwtService.generateAccessToken(principal)
        val refreshToken = jwtService.generateRefreshToken(principal)

        return AuthResponse(accessToken, refreshToken)
    }

    override fun login(request: LoginRequest): AuthResponse {
        val phone = request.phone.normalizePhone()

        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    phone.forDb,
                    request.password
                )
            )
        } catch (_: Exception) {
            throw BadCredentialsException("Неверные учетные данные")
        }

        val userDetails = userDetailsService.loadUserByUsername(phone.forDb)
        val principal = userDetails as UserPrincipal

        val accessToken = jwtService.generateAccessToken(principal)
        val refreshToken = jwtService.generateRefreshToken(principal)

        return AuthResponse(accessToken, refreshToken)
    }

    override fun logout(request: RefreshTokenRequest) {
        val refreshToken = request.refreshToken
        try {
            val remainingTime = jwtService.getRemainingExpiration(refreshToken)
            tokenBlacklistService.addToBlacklist(refreshToken, remainingTime)
        } catch (_: Exception) {}
    }

    override fun refresh(request: RefreshTokenRequest): AuthResponse {
        val refreshToken = request.refreshToken

        if (tokenBlacklistService.isBlacklisted(refreshToken)) {
            throw InvalidTokenException("Токен недействителен")
        }

        val userId = try {
            jwtService.extractUserId(refreshToken)
        } catch (_: ExpiredJwtException) {
            throw InvalidTokenException("Refresh-токен истек")
        } ?: throw InvalidTokenException("Невалидный refresh-токен")

        val user = userRepository.findById(userId)
            ?: throw InvalidTokenException("Пользователь не найден")

        val userDetails = userDetailsService.loadUserByUsername(user.phone)
        val principal = userDetails as UserPrincipal

        if (!jwtService.isTokenValid(refreshToken, principal)) {
            throw InvalidTokenException("Невалидный refresh-токен")
        }

        val remainingTime = jwtService.getRemainingExpiration(refreshToken)
        tokenBlacklistService.addToBlacklist(refreshToken, remainingTime)

        val newAccessToken = jwtService.generateAccessToken(principal)
        val newRefreshToken = jwtService.generateRefreshToken(principal)

        return AuthResponse(newAccessToken, newRefreshToken)
    }
}
