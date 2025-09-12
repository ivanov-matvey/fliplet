package dev.matvenoid.backend.application.dto

data class YandexGptResponse(
    val result: Result?
) {
    data class Result(
        val alternatives: List<Alternative>?
    )
    data class Alternative(
        val message: Message?,
        val status: String?
    )
    data class Message(
        val role: String?,
        val text: String?
    )
}