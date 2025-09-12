package dev.matvenoid.backend.application.controller

import dev.matvenoid.backend.application.dto.PageResponse
import dev.matvenoid.backend.application.dto.card.BulkCardRequest
import dev.matvenoid.backend.application.dto.card.CardRequest
import dev.matvenoid.backend.application.dto.card.CardResponse
import dev.matvenoid.backend.application.dto.card.PatchCardRequest
import dev.matvenoid.backend.application.service.CardService
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
@RequestMapping("/api/cards")
class CardController(
    private val cardService: CardService,
) {
    @GetMapping
    fun getOwnCards(
        @AuthenticationPrincipal jwt: Jwt,
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable,
    ): ResponseEntity<PageResponse<CardResponse>> {
        val userId = UUID.fromString(jwt.subject)
        val cards = cardService.getOwnCards(userId, pageable)
        return ResponseEntity(cards, HttpStatus.OK)
    }

    @GetMapping("/collection/{cardCollectionId}")
    fun getCardsByCardCollectionId(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable cardCollectionId: UUID,
        @PageableDefault(
            size = 10,
            sort = ["createdAt"],
            direction = Sort.Direction.DESC
        ) pageable: Pageable,
    ): ResponseEntity<PageResponse<CardResponse>> {
        val userId = UUID.fromString(jwt.subject)
        val cards = cardService.getCardsByCardCollectionId(userId, cardCollectionId, pageable)
        return ResponseEntity(cards, HttpStatus.OK)
    }

    @GetMapping("/{id}")
    fun getCard(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: UUID,
    ): ResponseEntity<CardResponse> {
        val userId = UUID.fromString(jwt.subject)
        val card = cardService.getCard(id, userId)
        return ResponseEntity(card, HttpStatus.OK)
    }

    @PostMapping
    fun createCard(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody @Valid request: CardRequest,
    ): ResponseEntity<CardResponse> {
        val userId = UUID.fromString(jwt.subject)
        val card = cardService.createCard(userId, request.cardCollectionId ,request)
        return ResponseEntity(card, HttpStatus.CREATED)
    }

    @PostMapping("/bulk")
    fun createCards(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody @Valid request: BulkCardRequest
    ): ResponseEntity<List<CardResponse>> {
        val userId = UUID.fromString(jwt.subject)
        val responses = cardService.createCards(userId, request.cards)
        return ResponseEntity(responses, HttpStatus.CREATED)
    }

    @PatchMapping("/{id}")
    fun patchCard(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: UUID,
        @RequestBody @Valid request: PatchCardRequest,
    ): ResponseEntity<CardResponse> {
        val userId = UUID.fromString(jwt.subject)
        val card = cardService.patchCard(id, userId, request)
        return ResponseEntity(card, HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun deleteCard(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable id: UUID
    ): ResponseEntity<Void> {
        val userId = UUID.fromString(jwt.subject)
        cardService.deleteCard(id, userId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}
