package dev.matvenoid.backend.infrastructure.persistence.mapper

import dev.matvenoid.backend.domain.model.Review
import dev.matvenoid.backend.infrastructure.persistence.entity.CardJpaEntity
import dev.matvenoid.backend.infrastructure.persistence.entity.ReviewJpaEntity
import dev.matvenoid.backend.infrastructure.persistence.entity.UserJpaEntity

fun Review.toJpaEntity(
    cardRef: CardJpaEntity,
    userRef: UserJpaEntity
): ReviewJpaEntity =
    ReviewJpaEntity(
        id = this.id,
        card = cardRef,
        user = userRef,
        reviewedAt = this.reviewedAt,
        quality = this.quality,
        prevIntervalDays = this.prevIntervalDays,
        newIntervalDays = this.newIntervalDays,
        prevEaseFactor = this.prevEaseFactor,
        newEaseFactor = this.newEaseFactor,
        prevRepetitions = this.prevRepetitions,
        newRepetitions = this.newRepetitions,
    )

fun ReviewJpaEntity.toDomain(): Review =
    Review.reconstitute(
        id = this.id,
        cardId = this.card.id,
        userId = this.user.id,
        reviewedAt = this.reviewedAt,
        quality = this.quality,
        prevIntervalDays = this.prevIntervalDays,
        newIntervalDays = this.newIntervalDays,
        prevEaseFactor = this.prevEaseFactor,
        newEaseFactor = this.newEaseFactor,
        prevRepetitions = this.prevRepetitions,
        newRepetitions = this.newRepetitions,
    )
