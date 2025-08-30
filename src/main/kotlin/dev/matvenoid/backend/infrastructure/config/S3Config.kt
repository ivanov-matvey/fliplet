package dev.matvenoid.backend.infrastructure.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

@Configuration
class S3Config(
    @param:Value($$"${yc.s3.region}") private val region: String,
    @param:Value($$"${yc.s3.endpoint}") private val endpoint: String
) {

    @Bean
    fun s3Client(): S3Client = S3Client.builder()
        .region(Region.of(region))
        .endpointOverride(URI.create(endpoint))
        .build()

    @Bean
    fun s3Presigner(): S3Presigner = S3Presigner.builder()
        .region(Region.of(region))
        .endpointOverride(URI.create(endpoint))
        .build()
}
