package dev.matvenoid.backend.application.dto.auth

import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequest(
    @field:NotBlank(message = "Refresh-токен не заполнен")
    val refreshToken: String,
)
