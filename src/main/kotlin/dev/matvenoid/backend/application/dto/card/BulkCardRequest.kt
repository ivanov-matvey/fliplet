package dev.matvenoid.backend.application.dto.card

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

data class BulkCardRequest(
    @field:NotNull(message = "Карточки не указаны")
    @field:Valid
    val cards: List<CardRequest>
)
