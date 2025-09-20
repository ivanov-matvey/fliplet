package dev.matvenoid.backend.application.dto.study

import java.util.UUID

data class StudyCardResponse(
    val id: UUID,
    val front: String,
    val back: String,
)
