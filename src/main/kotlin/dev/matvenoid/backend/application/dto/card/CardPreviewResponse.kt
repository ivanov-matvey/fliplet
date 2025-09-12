package dev.matvenoid.backend.application.dto.card

import kotlinx.serialization.Serializable

@Serializable
data class CardPreviewResponse(
    val front: String,
    val back: String
)
