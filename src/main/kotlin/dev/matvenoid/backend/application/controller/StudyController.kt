package dev.matvenoid.backend.application.controller

import dev.matvenoid.backend.application.dto.study.ReviewRequest
import dev.matvenoid.backend.application.dto.study.ReviewResponse
import dev.matvenoid.backend.application.dto.study.StudyCardResponse
import dev.matvenoid.backend.application.service.StudyService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/study")
class StudyController(
    private val studyService: StudyService,
) {
    @GetMapping("/next")
    fun getNextCard(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<StudyCardResponse> {
        val userId = UUID.fromString(jwt.subject)
        val card = studyService.getNextCard(userId)
            ?: return ResponseEntity(HttpStatus.NO_CONTENT)
        return ResponseEntity(card, HttpStatus.OK)
    }

    @PostMapping("/review")
    fun reviewCard(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody @Valid request: ReviewRequest,
    ): ResponseEntity<ReviewResponse> {
        val userId = UUID.fromString(jwt.subject)
        val review = studyService.reviewCard(userId, request)
        return ResponseEntity(review, HttpStatus.OK)
    }
}
