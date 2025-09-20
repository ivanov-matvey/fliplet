package dev.matvenoid.backend.infrastructure.persistence.repository.card_progress

import dev.matvenoid.backend.infrastructure.persistence.entity.CardProgressJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.time.OffsetDateTime
import java.util.UUID

interface CardProgressJpaRepository : JpaRepository<CardProgressJpaEntity, UUID> {
    fun findByCardIdAndUserId(cardId: UUID, userId: UUID): CardProgressJpaEntity?

    fun findAllByUserIdAndNextReviewAtBeforeOrderByNextReviewAtAsc(
        userId: UUID,
        now: OffsetDateTime
    ): List<CardProgressJpaEntity>
}
