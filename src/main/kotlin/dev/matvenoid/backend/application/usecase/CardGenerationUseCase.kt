package dev.matvenoid.backend.application.usecase

import dev.matvenoid.backend.application.dto.card.CardPreviewResponse

interface CardGenerationUseCase {
    fun generateFromPdf(pdf: ByteArray): List<CardPreviewResponse>
}