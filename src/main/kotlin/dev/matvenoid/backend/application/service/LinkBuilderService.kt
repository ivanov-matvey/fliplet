package dev.matvenoid.backend.application.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.time.Duration

@Service
class LinkBuilderService(
    @param:Value($$"${yc.s3.host}") private val host: String,
    @param:Value($$"${yc.s3.bucket}") private val bucket: String,
    private val presigner: S3Presigner
) {
    // Для фронта с настроенными CORS
    fun publicUrl(key: String): String =
        "https://$host/$bucket/$key"

    fun presignedGetUrl(key: String, ttlMinutes: Long = 10): String {
        val getReq = GetObjectRequest.builder().bucket(bucket).key(key).build()
        val pre = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(ttlMinutes))
            .getObjectRequest(getReq)
            .build()
        return presigner.presignGetObject(pre).url().toString()
    }
}
