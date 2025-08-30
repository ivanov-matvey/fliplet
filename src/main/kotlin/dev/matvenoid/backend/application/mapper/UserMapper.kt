package dev.matvenoid.backend.application.mapper

import dev.matvenoid.backend.application.dto.UserResponse
import dev.matvenoid.backend.application.service.LinkBuilderService
import dev.matvenoid.backend.domain.model.User
import org.springframework.stereotype.Component

@Component
class UserMapper(
    private val links: LinkBuilderService
) {
    fun toResponse(user: User): UserResponse =
        UserResponse(
            id = user.id,
            username = user.username,
            name = user.name,
            email = user.email,
            avatarUrl = user.avatarUrl.let { links.presignedGetUrl(it) }
        )
}
