package dev.matvenoid.backend.infrastructure.gpt.dto

data class CompletionRequest(
    val modelUri: String,
    val completionOptions: CompletionOptions,
    val messages: List<MessageDto>
)
