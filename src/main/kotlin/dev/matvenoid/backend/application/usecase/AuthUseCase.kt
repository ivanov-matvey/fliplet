package dev.matvenoid.backend.application.usecase

import dev.matvenoid.backend.application.dto.auth.AuthResponse
import dev.matvenoid.backend.application.dto.auth.LoginRequest
import dev.matvenoid.backend.application.dto.auth.RefreshTokenRequest
import dev.matvenoid.backend.application.dto.auth.RegistrationRequest
import dev.matvenoid.backend.application.dto.auth.ResendVerificationCodeRequest
import dev.matvenoid.backend.application.dto.auth.VerifyRequest


interface AuthUseCase {
    fun register(request: RegistrationRequest)
    fun login(request: LoginRequest): AuthResponse
    fun logout(request: RefreshTokenRequest)
    fun refresh(request: RefreshTokenRequest): AuthResponse
    fun verifyEmail(request: VerifyRequest)
    fun resendVerificationCode(request: ResendVerificationCodeRequest)
}
