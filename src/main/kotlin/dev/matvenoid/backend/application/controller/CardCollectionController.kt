package dev.matvenoid.backend.application.controller

import dev.matvenoid.backend.application.dto.CardCollectionResponse
import dev.matvenoid.backend.application.dto.PageResponse
import dev.matvenoid.backend.application.service.CardCollectionService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/collections")
class CardCollectionController(
    private val cardCollectionService: CardCollectionService,
) {
    @GetMapping()
    fun getCardCollections(
        @AuthenticationPrincipal jwt: Jwt,
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable,
    ): ResponseEntity<PageResponse<CardCollectionResponse>> {
        val id = UUID.fromString(jwt.subject)
        val cardCollections = cardCollectionService.getUserCardCollections(id, pageable)
        return ResponseEntity(cardCollections, HttpStatus.OK)
    }
}