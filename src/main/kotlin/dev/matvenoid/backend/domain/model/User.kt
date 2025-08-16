package dev.matvenoid.backend.domain.model

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class User private constructor(
    val id: UUID,
    val email: String,
    val name: String?,
    val username: String,
    val avatarUrl: String?,
    val passwordHash: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
) {
    companion object {
        fun create(
            username: String,
            name: String?,
            email: String,
            avatarUrl: String?,
            passwordHash: String,
        ): User {
            val now = OffsetDateTime.now(ZoneOffset.UTC)
            return User(
                id = UUID.randomUUID(),
                username = username,
                name = name,
                email = email,
                avatarUrl = avatarUrl,
                passwordHash = passwordHash,
                createdAt = now,
                updatedAt = now,
            )
        }

        fun reconstitute(
            id: UUID,
            username: String,
            name: String?,
            email: String,
            passwordHash: String,
            avatarUrl: String?,
            createdAt: OffsetDateTime,
            updatedAt: OffsetDateTime,
        ): User = User(
            id = id,
            username = username,
            name = name,
            email = email,
            avatarUrl = avatarUrl,
            passwordHash = passwordHash,
            createdAt = createdAt,
            updatedAt=updatedAt,
        )
    }
}
