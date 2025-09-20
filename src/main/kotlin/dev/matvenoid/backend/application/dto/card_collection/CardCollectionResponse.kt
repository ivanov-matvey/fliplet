package dev.matvenoid.backend.application.dto.card_collection

import java.util.UUID

data class CardCollectionResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val public: Boolean,
)