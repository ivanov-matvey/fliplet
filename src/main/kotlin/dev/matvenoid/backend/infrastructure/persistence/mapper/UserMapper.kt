package dev.matvenoid.backend.infrastructure.persistence.mapper

import dev.matvenoid.backend.domain.model.User
import dev.matvenoid.backend.infrastructure.persistence.entity.UserJpaEntity


fun User.toJpaEntity() = UserJpaEntity(
    username = this.username,
    usernameCi = this.username.lowercase(),
    name = this.name,
    phone = this.phone,
    avatarUrl = this.avatarUrl,
    passwordHash = this.passwordHash,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun UserJpaEntity.toDomain() = User.reconstitute(
    id = this.id!!,
    username = this.username,
    name = this.name,
    phone = this.phone,
    avatarUrl = this.avatarUrl,
    passwordHash = this.passwordHash,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)
