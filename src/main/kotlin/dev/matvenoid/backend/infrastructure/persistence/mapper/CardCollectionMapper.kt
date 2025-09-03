package dev.matvenoid.backend.infrastructure.persistence.mapper

import dev.matvenoid.backend.domain.model.CardCollection
import dev.matvenoid.backend.infrastructure.persistence.entity.CardCollectionJpaEntity
import dev.matvenoid.backend.infrastructure.persistence.entity.UserJpaEntity

fun CardCollection.toJpaEntity(userRef: UserJpaEntity) =
    CardCollectionJpaEntity(
        id = this.id,
        user = userRef,
        name = this.name,
        description = this.description,
        isPublic = this.isPublic,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )

fun CardCollectionJpaEntity.toDomain() =
    CardCollection.reconstitute(
        id = this.id,
        userId = this.user.id,
        name = this.name,
        description = this.description,
        isPublic = this.isPublic,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
