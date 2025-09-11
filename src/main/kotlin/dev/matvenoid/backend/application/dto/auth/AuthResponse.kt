package dev.matvenoid.backend.application.dto.auth

data class AuthResponse (
    val accessToken: String,
    val refreshToken: String,
)
