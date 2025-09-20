package dev.matvenoid.backend.domain.model

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class Review private constructor(
    val id: UUID,
    val cardId: UUID,
    val userId: UUID,
    val reviewedAt: OffsetDateTime,
    val quality: Short,
    val prevIntervalDays: Int,
    val newIntervalDays: Int,
    val prevEaseFactor: Double,
    val newEaseFactor: Double,
    val prevRepetitions: Int,
    val newRepetitions: Int,
) {
    companion object {
        fun create(
            cardId: UUID,
            userId: UUID,
            quality: Short,
            prevIntervalDays: Int,
            newIntervalDays: Int,
            prevEaseFactor: Double,
            newEaseFactor: Double,
            prevRepetitions: Int,
            newRepetitions: Int,
        ): Review {
            return Review(
                id = UUID.randomUUID(),
                cardId = cardId,
                userId = userId,
                reviewedAt = OffsetDateTime.now(ZoneOffset.UTC),
                quality = quality,
                prevIntervalDays = prevIntervalDays,
                newIntervalDays = newIntervalDays,
                prevEaseFactor = prevEaseFactor,
                newEaseFactor = newEaseFactor,
                prevRepetitions = prevRepetitions,
                newRepetitions = newRepetitions,
            )
        }

        fun reconstitute(
            id: UUID,
            cardId: UUID,
            userId: UUID,
            reviewedAt: OffsetDateTime,
            quality: Short,
            prevIntervalDays: Int,
            newIntervalDays: Int,
            prevEaseFactor: Double,
            newEaseFactor: Double,
            prevRepetitions: Int,
            newRepetitions: Int,
        ): Review = Review(
            id = id,
            cardId = cardId,
            userId = userId,
            reviewedAt = reviewedAt,
            quality = quality,
            prevIntervalDays = prevIntervalDays,
            newIntervalDays = newIntervalDays,
            prevEaseFactor = prevEaseFactor,
            newEaseFactor = newEaseFactor,
            prevRepetitions = prevRepetitions,
            newRepetitions = newRepetitions,
        )
    }

    fun copy(
        id: UUID = this.id,
        cardId: UUID = this.id,
        userId: UUID = this.userId,
        reviewedAt: OffsetDateTime = this.reviewedAt,
        quality: Short = this.quality,
        prevIntervalDays: Int = this.prevIntervalDays,
        newIntervalDays: Int = this.newIntervalDays,
        prevEaseFactor: Double = this.prevEaseFactor,
        newEaseFactor: Double = this.newEaseFactor,
        prevRepetitions: Int = this.prevRepetitions,
        newRepetitions: Int = this.newRepetitions,
    ): Review = Review(
        id = id,
        cardId = cardId,
        userId = userId,
        reviewedAt = reviewedAt,
        quality = quality,
        prevIntervalDays = prevIntervalDays,
        newIntervalDays = newIntervalDays,
        prevEaseFactor = prevEaseFactor,
        newEaseFactor = newEaseFactor,
        prevRepetitions = prevRepetitions,
        newRepetitions = newRepetitions,
    )
}
