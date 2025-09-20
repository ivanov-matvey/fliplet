package dev.matvenoid.backend.application.usecase

import dev.matvenoid.backend.application.dto.study.ReviewRequest
import dev.matvenoid.backend.application.dto.study.ReviewResponse
import dev.matvenoid.backend.application.dto.study.StudyCardResponse
import java.util.UUID

interface StudyUseCase {
    fun getNextCard(userId: UUID): StudyCardResponse?
    fun reviewCard(userId: UUID, request: ReviewRequest) : ReviewResponse
}
