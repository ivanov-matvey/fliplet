package dev.matvenoid.backend.application.controller

import dev.matvenoid.backend.application.dto.card_collection.CardCollectionResponse
import dev.matvenoid.backend.application.dto.PageResponse
import dev.matvenoid.backend.application.dto.card_collection.CardCollectionRequest
import dev.matvenoid.backend.application.dto.card_collection.PatchCardCollectionRequest
import dev.matvenoid.backend.application.service.CardCollectionService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/collections")
class CardCollectionController(
    private val cardCollectionService: CardCollectionService,
) {
    @GetMapping
    fun getOwnCardCollections(
        @AuthenticationPrincipal jwt: Jwt,
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable,
    ): ResponseEntity<PageResponse<CardCollectionResponse>> {
        val userId = UUID.fromString(jwt.subject)
        val cardCollections = cardCollectionService.getOwnCardCollections(userId, pageable)
        return ResponseEntity(cardCollections, HttpStatus.OK)
    }

    @GetMapping("/{username}")
    fun getCardCollectionsByUsername(
        @PathVariable username: String,
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable,
    ): ResponseEntity<PageResponse<CardCollectionResponse>> {
        val cardCollections = cardCollectionService.getCardCollectionsByUsername(username, pageable)
        return ResponseEntity(cardCollections, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getCardCollection(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: UUID,
    ): ResponseEntity<CardCollectionResponse> {
        val userId = UUID.fromString(jwt.subject)
        val cardCollection = cardCollectionService.getCardCollection(id, userId)
        return ResponseEntity(cardCollection, HttpStatus.OK)
    }

    @PostMapping
    fun createCardCollection(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody @Valid request: CardCollectionRequest,
    ): ResponseEntity<CardCollectionResponse> {
        val userId = UUID.fromString(jwt.subject)
        val cardCollection = cardCollectionService.createCardCollection(userId, request)
        return ResponseEntity(cardCollection, HttpStatus.CREATED)
    }

    @PatchMapping("/{id}")
    fun patchCardCollection(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: UUID,
        @RequestBody @Valid request: PatchCardCollectionRequest,
    ): ResponseEntity<CardCollectionResponse> {
        val userId = UUID.fromString(jwt.subject)
        val cardCollection = cardCollectionService.patchCardCollection(id, userId, request)
        return ResponseEntity(cardCollection, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteCardCollection(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        val userId = UUID.fromString(jwt.subject)
        cardCollectionService.deleteCardCollection(id, userId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
