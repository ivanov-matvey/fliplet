package dev.matvenoid.backend.application.dto.user

data class AvatarUploadInitRequest(
    val fileName: String,
    val contentType: String
)