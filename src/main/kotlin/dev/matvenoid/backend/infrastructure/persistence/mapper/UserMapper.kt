package dev.matvenoid.backend.infrastructure.persistence.mapper

import dev.matvenoid.backend.domain.model.User
import dev.matvenoid.backend.infrastructure.persistence.entity.UserJpaEntity


fun User.toJpaEntity() =
    UserJpaEntity(
        id = this.id,
        username = this.username,
        name = this.name,
        email = this.email,
        pendingEmail = this.pendingEmail,
        avatarUrl = this.avatarUrl,
        passwordHash = this.passwordHash,
        isEmailVerified = this.isEmailVerified,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        pendingEmailRequestedAt = this.pendingEmailRequestedAt,
    )

fun UserJpaEntity.toDomain() =
    User.reconstitute(
        id = this.id,
        username = this.username,
        name = this.name,
        email = this.email,
        pendingEmail = this.pendingEmail,
        avatarUrl = this.avatarUrl,
        passwordHash = this.passwordHash,
        isEmailVerified = this.isEmailVerified,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        pendingEmailRequestedAt = this.pendingEmailRequestedAt,
    )
