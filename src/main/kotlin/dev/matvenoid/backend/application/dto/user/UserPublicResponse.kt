package dev.matvenoid.backend.application.dto.user

data class UserPublicResponse(
    val username: String,
    val name: String?,
    val avatarUrl: String?,
)
