package dev.matvenoid.backend.application.dto.card_collection



data class PatchCardCollectionRequest(
    val name: String?,
    val description: String?,
    val public: Boolean?,
)
