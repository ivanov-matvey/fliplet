package dev.matvenoid.backend.application.controller

import dev.matvenoid.backend.application.dto.UserResponse
import dev.matvenoid.backend.application.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/me")
    fun getCurrentUser(
        @AuthenticationPrincipal(expression = "id") id: UUID
    ): ResponseEntity<UserResponse> {
        val user = userService.findById(id)
        return ResponseEntity(user, HttpStatus.OK)
    }

    @GetMapping("/{username}")
    fun getByUsername(
        @PathVariable username: String
    ): ResponseEntity<UserResponse> =
        ResponseEntity.ok(userService.findByUsername(username))
}
