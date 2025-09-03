package dev.matvenoid.backend.domain.model

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

class CardCollection private constructor(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val description: String?,
    val isPublic: Boolean,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
) {
    companion object {
        fun create(
            userId: UUID,
            name: String,
            description: String?,
            isPublic: Boolean = true,
        ): CardCollection {
            val now = OffsetDateTime.now(ZoneOffset.UTC)
            return CardCollection(
                id = UUID.randomUUID(),
                userId = userId,
                name = name,
                description = description,
                isPublic = isPublic,
                createdAt = now,
                updatedAt = now,
            )
        }

        fun reconstitute(
            id: UUID,
            userId: UUID,
            name: String,
            description: String?,
            isPublic: Boolean,
            createdAt: OffsetDateTime,
            updatedAt: OffsetDateTime,
        ): CardCollection = CardCollection(
            id = id,
            userId = userId,
            name = name,
            description = description,
            isPublic = isPublic,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
    }

    fun copy(
        id: UUID = this.id,
        userId: UUID = this.userId,
        name: String = this.name,
        description: String? = this.description,
        isPublic: Boolean = this.isPublic,
        createdAt: OffsetDateTime = this.createdAt,
        updatedAt: OffsetDateTime = this.updatedAt,
    ): CardCollection = CardCollection(
        id = id,
        userId = userId,
        name = name,
        description = description,
        isPublic = isPublic,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}