package dev.matvenoid.backend.application.dto

data class AuthResponse (
    val accessToken: String,
    val refreshToken: String,
)
