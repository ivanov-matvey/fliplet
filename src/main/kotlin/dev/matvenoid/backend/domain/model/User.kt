package dev.matvenoid.backend.domain.model

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class User private constructor(
    val id: UUID,
    val email: String,
    val pendingEmail: String?,
    val name: String?,
    val username: String,
    val avatarUrl: String?,
    val passwordHash: String,
    val isEmailVerified: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val pendingEmailRequestedAt: OffsetDateTime?,
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
                pendingEmail = null,
                avatarUrl = avatarUrl,
                passwordHash = passwordHash,
                isEmailVerified = false,
                createdAt = now,
                updatedAt = now,
                pendingEmailRequestedAt = null,
            )
        }

        fun reconstitute(
            id: UUID,
            username: String,
            name: String?,
            email: String,
            pendingEmail: String?,
            passwordHash: String,
            avatarUrl: String?,
            isEmailVerified: Boolean,
            createdAt: OffsetDateTime,
            updatedAt: OffsetDateTime,
            pendingEmailRequestedAt: OffsetDateTime?,
        ): User = User(
            id = id,
            username = username,
            name = name,
            email = email,
            pendingEmail = pendingEmail,
            avatarUrl = avatarUrl,
            passwordHash = passwordHash,
            isEmailVerified = isEmailVerified,
            createdAt = createdAt,
            updatedAt=updatedAt,
            pendingEmailRequestedAt = pendingEmailRequestedAt,
        )
    }

    fun copy(
        id: UUID = this.id,
        email: String = this.email,
        pendingEmail: String? = this.pendingEmail,
        name: String? = this.name,
        username: String = this.username,
        avatarUrl: String? = this.avatarUrl,
        passwordHash: String = this.passwordHash,
        isEmailVerified: Boolean = this.isEmailVerified,
        createdAt: OffsetDateTime = this.createdAt,
        updatedAt: OffsetDateTime = this.updatedAt,
        pendingEmailRequestedAt: OffsetDateTime? = this.pendingEmailRequestedAt,
    ): User = User(
        id = id,
        email = email,
        pendingEmail = pendingEmail,
        name = name,
        username = username,
        avatarUrl = avatarUrl,
        passwordHash = passwordHash,
        isEmailVerified = isEmailVerified,
        createdAt = createdAt,
        updatedAt = updatedAt,
        pendingEmailRequestedAt = pendingEmailRequestedAt,
    )
}
