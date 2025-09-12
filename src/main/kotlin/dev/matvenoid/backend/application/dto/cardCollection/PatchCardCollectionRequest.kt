package dev.matvenoid.backend.application.dto.cardCollection



data class PatchCardCollectionRequest(
    val name: String?,
    val description: String?,
    val public: Boolean?,
)
