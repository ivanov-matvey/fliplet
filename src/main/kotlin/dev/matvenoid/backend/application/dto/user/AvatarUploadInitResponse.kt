package dev.matvenoid.backend.application.dto.user

data class AvatarUploadInitResponse(
    val uploadUrl: String,
    val headers: Map<String, String>,
    val key: String,
    val method: String = "PUT",
)
