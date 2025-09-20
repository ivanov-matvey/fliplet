package dev.matvenoid.backend.infrastructure.persistence.mapper

import dev.matvenoid.backend.domain.model.CardProgress
import dev.matvenoid.backend.infrastructure.persistence.entity.CardJpaEntity
import dev.matvenoid.backend.infrastructure.persistence.entity.CardProgressJpaEntity
import dev.matvenoid.backend.infrastructure.persistence.entity.UserJpaEntity

fun CardProgress.toJpaEntity(
    cardRef: CardJpaEntity,
    userRef: UserJpaEntity
): CardProgressJpaEntity =
    CardProgressJpaEntity(
        id = this.id,
        card = cardRef,
        user = userRef,
        repetition = this.repetition,
        intervalDays = this.intervalDays,
        easeFactor = this.easeFactor,
        lastReviewAt = this.lastReviewAt,
        nextReviewAt = this.nextReviewAt,
    )

fun CardProgressJpaEntity.toDomain(): CardProgress =
    CardProgress.reconstitute(
        id = this.id,
        cardId = this.card.id,
        userId = this.user.id,
        repetition = this.repetition,
        intervalDays = this.intervalDays,
        easeFactor = this.easeFactor,
        lastReviewAt = this.lastReviewAt,
        nextReviewAt = this.nextReviewAt,
    )
