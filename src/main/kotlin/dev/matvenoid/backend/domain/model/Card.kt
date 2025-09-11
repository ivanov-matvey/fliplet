package dev.matvenoid.backend.domain.model

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class Card private constructor(
    val id: UUID,
    val cardCollectionId: UUID,
    val userId: UUID,
    val front: String,
    val back: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
) {
    companion object {
        fun create(
            cardCollectionId: UUID,
            userId: UUID,
            front: String,
            back: String,
        ): Card {
            val now = OffsetDateTime.now(ZoneOffset.UTC)
            return Card(
                id = UUID.randomUUID(),
                userId = userId,
                cardCollectionId = cardCollectionId,
                front = front,
                back = back,
                createdAt = now,
                updatedAt = now,
            )
        }

        fun reconstitute(
            id: UUID,
            cardCollectionId: UUID,
            userId: UUID,
            front: String,
            back: String,
            createdAt: OffsetDateTime,
            updatedAt: OffsetDateTime,
        ): Card = Card(
            id = id,
            cardCollectionId = cardCollectionId,
            userId = userId,
            front = front,
            back = back,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }

    fun copy(
        id: UUID = this.id,
        cardCollectionId: UUID = this.cardCollectionId,
        userId: UUID = this.userId,
        front: String = this.front,
        back: String = this.back,
        createdAt: OffsetDateTime = this.createdAt,
        updatedAt: OffsetDateTime = this.updatedAt,
    ): Card = Card(
        id = id,
        cardCollectionId = cardCollectionId,
        userId = userId,
        front = front,
        back = back,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}
