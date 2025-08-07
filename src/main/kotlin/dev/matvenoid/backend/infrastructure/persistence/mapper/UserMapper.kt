package dev.matvenoid.backend.infrastructure.persistence.mapper

import dev.matvenoid.backend.domain.model.User
import dev.matvenoid.backend.infrastructure.persistence.entity.UserJpaEntity


fun User.toJpaEntity() = UserJpaEntity(
    phone = this.phone,
    passwordHash = this.passwordHash,
    name = this.name,
    avatarUrl = this.avatarUrl,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)

fun UserJpaEntity.toDomain() = User.reconstitute(
    id = this.id!!,
    phone = this.phone,
    passwordHash = this.passwordHash,
    name = this.name,
    avatarUrl = this.avatarUrl,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
)
