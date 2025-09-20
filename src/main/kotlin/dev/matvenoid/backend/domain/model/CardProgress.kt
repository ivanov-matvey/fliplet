package dev.matvenoid.backend.domain.model

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.math.roundToInt

class CardProgress private constructor(
    val id: UUID,
    val cardId: UUID,
    val userId: UUID,
    val repetition: Int,
    val intervalDays: Int,
    val easeFactor: Double,
    val lastReviewAt: OffsetDateTime?,
    val nextReviewAt: OffsetDateTime,
) {
    companion object {
        fun create(
            cardId: UUID,
            userId: UUID
        ): CardProgress {
            val now = OffsetDateTime.now(ZoneOffset.UTC)
            return CardProgress(
                id = UUID.randomUUID(),
                cardId = cardId,
                userId = userId,
                repetition = 0,
                intervalDays = 1,
                easeFactor = 2.5,
                lastReviewAt = null,
                nextReviewAt = now,
            )
        }

        fun reconstitute(
            id: UUID,
            cardId: UUID,
            userId: UUID,
            repetition: Int,
            intervalDays: Int,
            easeFactor: Double,
            lastReviewAt: OffsetDateTime?,
            nextReviewAt: OffsetDateTime,
        ): CardProgress = CardProgress(
            id = id,
            cardId = cardId,
            userId = userId,
            repetition = repetition,
            intervalDays = intervalDays,
            easeFactor = easeFactor,
            lastReviewAt = lastReviewAt,
            nextReviewAt = nextReviewAt,
        )
    }

    fun copy(
        id: UUID = this.id,
        cardId: UUID = this.cardId,
        userId: UUID = this.userId,
        repetition: Int = this.repetition,
        intervalDays: Int = this.intervalDays,
        easeFactor: Double = this.easeFactor,
        lastReviewAt: OffsetDateTime? = this.lastReviewAt,
        nextReviewAt: OffsetDateTime = this.nextReviewAt,
    ): CardProgress = CardProgress(
        id = id,
        cardId = cardId,
        userId = userId,
        repetition = repetition,
        intervalDays = intervalDays,
        easeFactor = easeFactor,
        lastReviewAt = lastReviewAt,
        nextReviewAt = nextReviewAt,
    )

    fun review(quality: Short): CardProgress {
        val q = quality.coerceIn(0, 5)
        val now = OffsetDateTime.now(ZoneOffset.UTC)

        val updatedEase = if (q < 3) {
            (easeFactor - 0.2).coerceAtLeast(1.3)
        } else {
            val delta = 0.1 - (5 - q) * (0.08 + (5 - q) * 0.02)
            (easeFactor + delta).coerceAtLeast(1.3)
        }

        val (newRepetition, newInterval) = if (q < 3) {
            0 to 1
        } else {
            val rep = repetition + 1
            val interval = when (rep) {
                1 -> 1
                2 -> 6
                else -> (intervalDays * updatedEase).roundToInt()
            }
            rep to interval
        }

        return copy(
            repetition   = newRepetition,
            intervalDays = newInterval,
            easeFactor   = updatedEase,
            lastReviewAt = now,
            nextReviewAt = now.plusDays(newInterval.toLong())
        )
    }
}
