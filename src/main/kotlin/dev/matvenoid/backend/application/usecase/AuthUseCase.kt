package dev.matvenoid.backend.application.usecase

import dev.matvenoid.backend.application.dto.AuthResponse
import dev.matvenoid.backend.application.dto.LoginRequest
import dev.matvenoid.backend.application.dto.RefreshTokenRequest
import dev.matvenoid.backend.application.dto.RegistrationRequest


interface AuthUseCase {
    fun register(request: RegistrationRequest): AuthResponse
    fun login(request: LoginRequest): AuthResponse
    fun logout(request: RefreshTokenRequest)
    fun refresh(request: RefreshTokenRequest): AuthResponse
}
