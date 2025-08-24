package dev.matvenoid.backend.application.controller

import dev.matvenoid.backend.application.dto.AuthResponse
import dev.matvenoid.backend.application.dto.LoginRequest
import dev.matvenoid.backend.application.dto.RefreshTokenRequest
import dev.matvenoid.backend.application.dto.RegistrationRequest
import dev.matvenoid.backend.application.dto.ResendVerificationCodeRequest
import dev.matvenoid.backend.application.dto.VerifyRequest
import dev.matvenoid.backend.application.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {

    @PostMapping("/register")
    fun register(
        @RequestBody @Valid request: RegistrationRequest
    ): ResponseEntity<AuthResponse> {
        authService.register(request)
        return ResponseEntity( HttpStatus.ACCEPTED)
    }

    @PostMapping("/verify-email")
    fun verify(
        @RequestBody @Valid request: VerifyRequest
    ): ResponseEntity<AuthResponse> {
        authService.verifyEmail(request)
        return ResponseEntity(HttpStatus.OK)
    }

    @PostMapping("/resend-verification-code")
    fun resendVerificationCode(
        @RequestBody @Valid request: ResendVerificationCodeRequest
    ): ResponseEntity<Void> {
        authService.resendVerificationCode(request)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody @Valid request: LoginRequest
    ): ResponseEntity<AuthResponse> {
        val response = authService.login(request)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping("/refresh")
    fun refresh(
        @RequestBody @Valid request: RefreshTokenRequest
    ): ResponseEntity<AuthResponse> {
        val response = authService.refresh(request)
        return ResponseEntity(response, HttpStatus.OK)
    }

    @PostMapping("/logout")
    fun logout(
        @RequestBody @Valid request: RefreshTokenRequest
    ): ResponseEntity<Void> {
        authService.logout(request)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
