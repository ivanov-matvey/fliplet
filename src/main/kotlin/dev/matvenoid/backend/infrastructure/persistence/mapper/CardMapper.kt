package dev.matvenoid.backend.infrastructure.persistence.mapper

import dev.matvenoid.backend.domain.model.Card
import dev.matvenoid.backend.infrastructure.persistence.entity.CardCollectionJpaEntity
import dev.matvenoid.backend.infrastructure.persistence.entity.CardJpaEntity

fun Card.toJpaEntity(cardCollectionRef: CardCollectionJpaEntity): CardJpaEntity =
    CardJpaEntity(
        id = this.id,
        cardCollection = cardCollectionRef,
        front = this.front,
        back = this.back,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )

fun CardJpaEntity.toDomain(): Card =
    Card.reconstitute(
        id = this.id,
        cardCollectionId = this.cardCollection.id,
        userId = this.cardCollection.user.id,
        front = this.front,
        back = this.back,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
