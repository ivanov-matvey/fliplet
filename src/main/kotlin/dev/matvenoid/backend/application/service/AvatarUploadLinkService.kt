package dev.matvenoid.backend.application.service

import dev.matvenoid.backend.application.dto.AvatarUploadInitResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.time.Duration
import java.util.UUID

@Service
class AvatarUploadLinkService(
    private val presigner: S3Presigner,
    @param:Value($$"${yc.s3.bucket}")
    private val bucket: String,
    @param:Value($$"#{'${yc.avatars.allowedContentTypes}'.split(',')}")
    private val allowedContentTypes: List<String>,
    @param:Value($$"${yc.avatars.keyPrefix}")
    private val keyPrefix: String
) {
    private val extByType = mapOf(
        "image/jpg" to "jpg",
        "image/jpeg" to "jpg",
        "image/png"  to "png",
        "image/webp" to "webp",
    )

    fun createPresignedPut(
        id: UUID,
        fileName: String,
        contentType: String,
    ): AvatarUploadInitResponse {
        if (contentType !in allowedContentTypes) {
            throw BadCredentialsException("Недопустимый Content-Type")
        }

        val base = fileName.substringBeforeLast('.')
            .replace("""[^\w.\-]""".toRegex(), "_")
        val ext = extByType[contentType]
        val key = "$keyPrefix/$id/$base.$ext"

        val putObject = PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType)
            .build()

        val presignReq = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10))
            .putObjectRequest(putObject)
            .build()

        val presigned = presigner.presignPutObject(presignReq)
        val headers: Map<String, String> = presigned.signedHeaders()
            .mapValues { (_, value) -> value.joinToString(",")} +
                ("Content-Type" to contentType)

        return AvatarUploadInitResponse(
            uploadUrl = presigned.url().toString(),
            headers = headers,
            key = key,
            method = "PUT",
            contentType = contentType
        )
    }
}
