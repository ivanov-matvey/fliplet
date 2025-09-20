package dev.matvenoid.backend.domain.repository

import dev.matvenoid.backend.domain.model.CardProgress
import java.time.OffsetDateTime
import java.util.UUID

interface CardProgressRepository {
    fun findByCardIdAndUserId(cardId: UUID, userId: UUID): CardProgress?
    fun findFirstDueByUserId(userId: UUID, now: OffsetDateTime): CardProgress?
    fun save(cardProgress: CardProgress): CardProgress
    fun saveAll(cardProgresses: List<CardProgress>): List<CardProgress>
}
