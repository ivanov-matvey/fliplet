package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.application.dto.card.CardPreviewResponse
import dev.matvenoid.backend.application.mapper.GptCardParser
import dev.matvenoid.backend.application.usecase.CardGenerationUseCase
import dev.matvenoid.backend.infrastructure.gpt.YandexGptClient
import dev.matvenoid.backend.infrastructure.gpt.dto.MessageDto
import dev.matvenoid.backend.infrastructure.pdf.PdfTextUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CardGenerationService(
    private val gpt: YandexGptClient,
    private val pdfUtil: PdfTextUtil,
    @param:Value($$"${yc.gpt.system-prompt}") private val systemPrompt: String
): CardGenerationUseCase {
    override fun generateFromPdf(pdf: ByteArray): List<CardPreviewResponse> =
        pdfUtil.splitForLlm(pdf).flatMap { chunk ->
            val messages = listOf(
                MessageDto(role = "system", text = systemPrompt),
                MessageDto(role = "user",   text = chunk)
            )
            val response = gpt.complete(messages)
            GptCardParser.toCards(response)
        }
}
