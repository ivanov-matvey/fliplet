package dev.matvenoid.backend.application.usecase

import dev.matvenoid.backend.application.dto.AuthResponse
import dev.matvenoid.backend.application.dto.LoginRequest
import dev.matvenoid.backend.application.dto.RefreshTokenRequest
import dev.matvenoid.backend.application.dto.RegistrationRequest
import dev.matvenoid.backend.application.dto.ResendVerificationCodeRequest
import dev.matvenoid.backend.application.dto.VerifyRequest


interface AuthUseCase {
    fun register(request: RegistrationRequest)
    fun login(request: LoginRequest): AuthResponse
    fun logout(request: RefreshTokenRequest)
    fun refresh(request: RefreshTokenRequest): AuthResponse
    fun verifyEmail(request: VerifyRequest)
    fun resendVerificationCode(request: ResendVerificationCodeRequest)
}
