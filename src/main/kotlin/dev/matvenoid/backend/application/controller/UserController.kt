package dev.matvenoid.backend.application.controller

import dev.matvenoid.backend.application.dto.UpdateEmailRequest
import dev.matvenoid.backend.application.dto.UpdateNameRequest
import dev.matvenoid.backend.application.dto.UserResponse
import dev.matvenoid.backend.application.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/me")
    fun getCurrentUser(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<UserResponse> {
        val id = UUID.fromString(jwt.subject)
        val user = userService.findById(id)
        return ResponseEntity(user, HttpStatus.OK)
    }

    @GetMapping("/{username}")
    fun getByUsername(
        @PathVariable username: String,
    ): ResponseEntity<UserResponse> {
        val user = userService.findByUsername(username)
        return ResponseEntity(user, HttpStatus.OK)
    }

    @PatchMapping("/me/email")
    fun patchEmail(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody @Valid request: UpdateEmailRequest,
    ): ResponseEntity<Void> {
        val id = UUID.fromString(jwt.subject)
        userService.updateEmail(id, request.email)
        return ResponseEntity(HttpStatus.OK)
    }

    @PatchMapping("/me/name")
    fun patchName(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody @Valid request: UpdateNameRequest,
    ): ResponseEntity<UserResponse> {
        val id = UUID.fromString(jwt.subject)
        val updatedUser = userService.updateName(id, request.name)
        return ResponseEntity(updatedUser, HttpStatus.OK)
    }

    @PatchMapping(("/me/username"))
    fun patchUsername(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<UserResponse> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    @PatchMapping("/me/avatar")
    fun patchAvatar(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<UserResponse> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }

    @PatchMapping("/me/password")
    fun patchPassword(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<UserResponse> {
        return ResponseEntity(HttpStatus.NOT_IMPLEMENTED)
    }
}
