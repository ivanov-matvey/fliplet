package dev.matvenoid.backend.application.mapper

import dev.matvenoid.backend.application.dto.UserResponse
import dev.matvenoid.backend.domain.model.User

fun User.toResponse() = UserResponse(
    id = id,
    username = username,
    name = name,
    email = email,
    avatarUrl = avatarUrl
)
