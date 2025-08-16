package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.application.dto.AuthResponse
import dev.matvenoid.backend.application.dto.LoginRequest
import dev.matvenoid.backend.application.dto.RefreshTokenRequest
import dev.matvenoid.backend.application.dto.RegistrationRequest
import dev.matvenoid.backend.application.security.UserPrincipal
import dev.matvenoid.backend.application.usecase.AuthUseCase
import dev.matvenoid.backend.application.util.UsernameGenerator
import dev.matvenoid.backend.domain.exception.UserAlreadyExistsException
import dev.matvenoid.backend.domain.model.User
import dev.matvenoid.backend.domain.repository.UserRepository
import io.jsonwebtoken.JwtException
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
        if (userRepository.existsByEmail(request.email)) {
            throw UserAlreadyExistsException(
                "Пользователь с электронной почтой ${request.email} уже существует"
            )
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

        val userDetails = userDetailsService.loadUserByUsername(savedUser.email)
        val principal = userDetails as UserPrincipal

        val accessToken = jwtService.generateAccessToken(principal)
        val refreshToken = jwtService.generateRefreshToken(principal)

        return AuthResponse(accessToken, refreshToken)
    }

    override fun login(request: LoginRequest): AuthResponse {
        try {
            authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    request.email,
                    request.password
                )
            )
        } catch (_: Exception) {
            throw BadCredentialsException("Неверный адрес электронной почты или пароль")
        }

        val userDetails = userDetailsService.loadUserByUsername(request.email)
        val principal = userDetails as UserPrincipal

        val accessToken = jwtService.generateAccessToken(principal)
        val refreshToken = jwtService.generateRefreshToken(principal)

        return AuthResponse(accessToken, refreshToken)
    }

    override fun logout(request: RefreshTokenRequest) {
        val refreshToken = request.refreshToken
        val remainingTime = jwtService.getRemainingExpiration(refreshToken)
        tokenBlacklistService.addToBlacklist(refreshToken, remainingTime)
    }

    override fun refresh(request: RefreshTokenRequest): AuthResponse {
        val refreshToken = request.refreshToken

        val userId = jwtService.extractUserId(refreshToken)
            ?: throw JwtException("Не удалось извлечь ID пользователя")

        val user = userRepository.findById(userId)
            ?: throw JwtException("Пользователь не найден")

        val userDetails = userDetailsService.loadUserByUsername(user.email)
        val principal = userDetails as UserPrincipal

        val remainingTime = jwtService.getRemainingExpiration(refreshToken)
        tokenBlacklistService.addToBlacklist(refreshToken, remainingTime)

        val newAccessToken = jwtService.generateAccessToken(principal)
        val newRefreshToken = jwtService.generateRefreshToken(principal)

        return AuthResponse(newAccessToken, newRefreshToken)
    }
}
