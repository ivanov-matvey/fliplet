package dev.matvenoid.backend.application.mapper

import dev.matvenoid.backend.application.dto.YandexGptResponse
import dev.matvenoid.backend.application.dto.card.CardPreviewResponse
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

object GptCardParser {
    private val logger = LoggerFactory.getLogger(GptCardParser::class.java)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun toCards(response: YandexGptResponse): List<CardPreviewResponse> {
        val text = response.result
            ?.alternatives
            ?.firstOrNull()
            ?.message
            ?.text
            ?: return emptyList()

        val arrayJson = extractJsonArray(text)
        return try {
            json.decodeFromString<List<CardPreviewResponse>>(arrayJson)
        } catch (ex: Exception) {
            logger.warn("Cannot parse GPT response", ex)
            emptyList()
        }
    }

    private fun extractJsonArray(raw: String): String {
        val cleaned = raw
            .trim()
            .removePrefix("```").removeSuffix("```")
            .removePrefix("~~~").removeSuffix("~~~")
            .trim()
        val start = cleaned.indexOf("[")
        val end = cleaned.lastIndexOf("]")
        return if (start != -1 && end != -1 && end > start) {
            cleaned.substring(start, end + 1)
        } else "[]"
    }
}
