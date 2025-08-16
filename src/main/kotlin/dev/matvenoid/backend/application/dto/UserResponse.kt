package dev.matvenoid.backend.application.dto

import java.util.UUID

data class UserResponse(
    val id: UUID,
    val username: String,
    val name: String?,
    val email: String,
    val avatarUrl: String?,
)
