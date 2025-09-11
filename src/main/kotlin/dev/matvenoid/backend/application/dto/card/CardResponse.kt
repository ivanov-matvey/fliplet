package dev.matvenoid.backend.application.dto.card

import java.util.UUID

data class CardResponse(
    val id: UUID,
    val front: String,
    val back: String,
)
