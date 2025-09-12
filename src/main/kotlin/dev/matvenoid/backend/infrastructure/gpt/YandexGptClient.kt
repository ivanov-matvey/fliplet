package dev.matvenoid.backend.infrastructure.gpt

import dev.matvenoid.backend.application.dto.YandexGptResponse
import dev.matvenoid.backend.infrastructure.gpt.dto.CompletionOptions
import dev.matvenoid.backend.infrastructure.gpt.dto.CompletionRequest
import dev.matvenoid.backend.infrastructure.gpt.dto.MessageDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class YandexGptClient(
    private val restTemplate: RestTemplate,
    @param:Value($$"${yc.gpt.url}") private val url: String,
    @param:Value($$"${yc.gpt.api-key}") private val apiKey: String,
    @param:Value($$"${yc.gpt.model-uri}") private val modelUri: String,
    @param:Value($$"${yc.gpt.options.temperature:0.3}") private val temperature: Double,
    @param:Value($$"${yc.gpt.options.max-tokens:2000}") private val maxTokens: Int
) {
    private val logger = LoggerFactory.getLogger(YandexGptClient::class.java)

    fun complete(messages: List<MessageDto>): YandexGptResponse {
        val options = CompletionOptions(
            temperature = temperature,
            maxTokens = maxTokens.toString(),
        )
        val request = CompletionRequest(
            modelUri = modelUri,
            completionOptions = options,
            messages = messages
        )

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            set("Authorization", "Api-Key $apiKey")
        }

        val entity = HttpEntity(request, headers)

        return restTemplate
            .exchange(
                url,
                HttpMethod.POST,
                entity,
                YandexGptResponse::class.java
            )
            .body ?: run {
                logger.warn("Empty GPT response")
                throw IllegalStateException("Empty GPT response")
            }
    }
}
