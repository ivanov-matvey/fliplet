package dev.matvenoid.backend.domain.model

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class User private constructor(
    val id: UUID,
    val phone: String,
    val name: String,
    val passwordHash: String,
    val avatarUrl: String?,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
) {
    companion object {
        fun create(
            phone: String,
            passwordHash: String,
            name: String,
            avatarUrl: String?,
        ): User {
            val now = OffsetDateTime.now(ZoneOffset.UTC)
            return User(
                id = UUID.randomUUID(),
                phone = phone,
                passwordHash = passwordHash,
                name = name,
                avatarUrl = avatarUrl,
                createdAt = now,
                updatedAt = now,
            )
        }

        fun reconstitute(
            id: UUID,
            phone: String,
            passwordHash: String,
            name: String,
            avatarUrl: String?,
            createdAt: OffsetDateTime,
            updatedAt: OffsetDateTime,
        ): User = User(
            id = id,
            phone = phone,
            name = name,
            passwordHash = passwordHash,
            avatarUrl = avatarUrl,
            createdAt = createdAt,
            updatedAt=updatedAt,
        )
    }
}
