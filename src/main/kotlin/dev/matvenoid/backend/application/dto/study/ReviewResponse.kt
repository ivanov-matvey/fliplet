package dev.matvenoid.backend.application.dto.study

import java.util.UUID

data class ReviewResponse(
    val id: UUID,
    val quality: Short,
    val nextReviewAt: String,
)
