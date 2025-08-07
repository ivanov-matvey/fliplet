package dev.matvenoid.backend.application.service

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import dev.matvenoid.backend.application.dto.AuthResponse
import dev.matvenoid.backend.application.dto.LoginRequest
import dev.matvenoid.backend.application.dto.RefreshTokenRequest
import dev.matvenoid.backend.application.dto.RegistrationRequest
import dev.matvenoid.backend.application.usecase.AuthUseCase
import dev.matvenoid.backend.domain.exception.InvalidTokenException
import dev.matvenoid.backend.domain.model.User
import dev.matvenoid.backend.domain.exception.UserAlreadyExistsException
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
) : AuthUseCase {

    private data class NormalizedPhone(val forDb: String, val forDisplay: String)

    override fun register(request: RegistrationRequest): AuthResponse {
        val phone = normalizePhoneNumber(request.phone)

        if (userRepository.existsByPhone(phone.forDb)) {
            throw UserAlreadyExistsException(
                "Пользователь с телефоном ${phone.forDisplay} уже существует"
            )
        }

        val user = User.create(
            phone = phone.forDb,
            passwordHash = passwordEncoder.encode(request.password),
            name = "Пользователь",
            avatarUrl = null
        )

        val savedUser = userRepository.save(user)

        val userDetails = userDetailsService.loadUserByUsername(savedUser.phone)
        val accessToken = jwtService.generateAccessToken(userDetails)
        val refreshToken = jwtService.generateRefreshToken(userDetails)

        return AuthResponse(accessToken, refreshToken)
    }

    override fun login(request: LoginRequest): AuthResponse {
        val phone = normalizePhoneNumber(request.phone)

        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                phone.forDb,
                request.password
            )
        )

        val userDetails = userDetailsService.loadUserByUsername(phone.forDb)

        val accessToken = jwtService.generateAccessToken(userDetails)
        val refreshToken = jwtService.generateRefreshToken(userDetails)

        return AuthResponse(accessToken, refreshToken)
    }


    override fun logout(request: RefreshTokenRequest) {
        val refreshToken = request.refreshToken

        try {
            val remainingTime = jwtService.getRemainingExpiration(refreshToken)
            tokenBlacklistService.addToBlacklist(refreshToken, remainingTime)
        } catch (e: Exception) {}
    }

    override fun refresh(request: RefreshTokenRequest): AuthResponse {
        val refreshToken = request.refreshToken

        if (tokenBlacklistService.isBlacklisted(refreshToken)) {
            throw InvalidTokenException("Токен недействителен")
        }

        val userPhone = try {
            jwtService.extractUsername(refreshToken)
        } catch (e: ExpiredJwtException) {
            throw InvalidTokenException("Refresh-токен истек")
        } ?: throw InvalidTokenException("Невалидный refresh-токен")

        val userDetails = userDetailsService.loadUserByUsername(userPhone)

        if (!jwtService.isTokenValid(refreshToken, userDetails)) {
            throw InvalidTokenException("Невалидный refresh-токен")
        }

        val remainingTime = jwtService.getRemainingExpiration(refreshToken)
        tokenBlacklistService.addToBlacklist(refreshToken, remainingTime)

        val newAccessToken = jwtService.generateAccessToken(userDetails)
        val newRefreshToken = jwtService.generateRefreshToken(userDetails)

        return AuthResponse(newAccessToken, newRefreshToken)
    }

    private fun normalizePhoneNumber(phone: String): NormalizedPhone {
        return try {
            val phoneUtil = PhoneNumberUtil.getInstance()
            val numberProto = phoneUtil.parse(phone, "RU")

            val dbFormat = numberProto.nationalNumber.toString()
            val displayFormat = phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164)

            NormalizedPhone(forDb = dbFormat, forDisplay = displayFormat)
        } catch (e: NumberParseException) {
            throw BadCredentialsException("Некорректный формат номера телефона: $phone")
        }
    }
}
