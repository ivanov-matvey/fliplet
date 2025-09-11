package dev.matvenoid.backend.application.dto.cardCollection

import java.util.UUID

data class CardCollectionResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val isPublic: Boolean,
)