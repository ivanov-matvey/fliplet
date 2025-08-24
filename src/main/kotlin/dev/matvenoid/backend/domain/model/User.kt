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
    val isEmailVerified: Boolean,
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
                isEmailVerified = false,
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
            isEmailVerified: Boolean,
            createdAt: OffsetDateTime,
            updatedAt: OffsetDateTime,
        ): User = User(
            id = id,
            username = username,
            name = name,
            email = email,
            avatarUrl = avatarUrl,
            passwordHash = passwordHash,
            isEmailVerified = isEmailVerified,
            createdAt = createdAt,
            updatedAt=updatedAt,
        )
    }

    fun copy(
        id: UUID = this.id,
        email: String = this.email,
        name: String? = this.name,
        username: String = this.username,
        avatarUrl: String? = this.avatarUrl,
        passwordHash: String = this.passwordHash,
        isEmailVerified: Boolean = this.isEmailVerified,
        createdAt: OffsetDateTime = this.createdAt,
        updatedAt: OffsetDateTime = this.updatedAt
    ): User = User(
        id = id,
        email = email,
        name = name,
        username = username,
        avatarUrl = avatarUrl,
        passwordHash = passwordHash,
        isEmailVerified = isEmailVerified,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
