package dev.matvenoid.backend.application.dto

data class AvatarUploadInitRequest(
    val fileName: String,
    val contentType: String
)