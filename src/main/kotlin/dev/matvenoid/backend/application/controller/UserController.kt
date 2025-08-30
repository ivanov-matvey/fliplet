package dev.matvenoid.backend.application.controller

import dev.matvenoid.backend.application.dto.AvatarConfirmRequest
import dev.matvenoid.backend.application.dto.AvatarUploadInitRequest
import dev.matvenoid.backend.application.dto.AvatarUploadInitResponse
import dev.matvenoid.backend.application.dto.UpdateEmailRequest
import dev.matvenoid.backend.application.dto.UpdateNameRequest
import dev.matvenoid.backend.application.dto.UpdatePasswordRequest
import dev.matvenoid.backend.application.dto.UpdateUsernameRequest
import dev.matvenoid.backend.application.dto.UserResponse
import dev.matvenoid.backend.application.service.AvatarUploadLinkService
import dev.matvenoid.backend.application.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val avatarUploadLinkService: AvatarUploadLinkService,
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
        userService.updateEmail(id, request)
        return ResponseEntity(HttpStatus.OK)
    }

    @PatchMapping("/me/name")
    fun patchName(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody @Valid request: UpdateNameRequest,
    ): ResponseEntity<UserResponse> {
        val id = UUID.fromString(jwt.subject)
        val updatedUser = userService.updateName(id, request)
        return ResponseEntity(updatedUser, HttpStatus.OK)
    }

    @PatchMapping(("/me/username"))
    fun patchUsername(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody @Valid request: UpdateUsernameRequest,
    ): ResponseEntity<UserResponse> {
        val id = UUID.fromString(jwt.subject)
        val updatedUser = userService.updateUsername(id, request)
        return ResponseEntity(updatedUser, HttpStatus.OK)
    }

    @PatchMapping("/me/avatar")
    fun initAvatarUpload(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody @Valid request: AvatarUploadInitRequest,
    ): ResponseEntity<AvatarUploadInitResponse> {
        val id = UUID.fromString(jwt.subject)
        val presigned = avatarUploadLinkService.createPresignedPut(
            id,
            request.fileName,
            request.contentType,
        )
        return ResponseEntity(presigned, HttpStatus.OK)
    }

    @PostMapping("/me/avatar/confirm")
    fun confirmAvatar(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody @Valid request: AvatarConfirmRequest,
    ): ResponseEntity<UserResponse> {
        val id = UUID.fromString(jwt.subject)
        if (!request.key.startsWith("avatars/$id/")) {
            return ResponseEntity(HttpStatus.FORBIDDEN)
        }
        val updated = userService.updateAvatarUrl(id, request.key)
        return ResponseEntity(updated, HttpStatus.OK)
    }

    @PatchMapping("/me/password")
    fun patchPassword(
        @AuthenticationPrincipal jwt: Jwt,
        @RequestBody @Valid request: UpdatePasswordRequest,
    ): ResponseEntity<UserResponse> {
        val id = UUID.fromString(jwt.subject)
        val updatedUser = userService.updatePassword(id, request)
        return ResponseEntity(updatedUser, HttpStatus.OK)
    }
}
