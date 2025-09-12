package dev.matvenoid.backend.infrastructure.gpt.dto

data class CompletionOptions(
    val temperature: Double,
    val maxTokens: String,
)
