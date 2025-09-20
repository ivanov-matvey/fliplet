package dev.matvenoid.backend.application.dto.study

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.util.UUID

data class ReviewRequest(
    val cardId: UUID,
    @field:Min(0)
    @field:Max(5)
    val quality: Short,
)
