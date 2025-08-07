package dev.matvenoid.backend.application.dto

import dev.matvenoid.backend.application.validation.PhoneNumber
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegistrationRequest(
    @field:NotBlank(message = "Телефон не заполнен")
    @field:PhoneNumber(message = "Некорректный номер телефона")
    val phone: String,

    @field:NotBlank(message = "Пароль не заполнен")
    @field:Size(
        min = 8, max = 64,
        message = "Пароль должен быть от 8 до 64 символов",
    )
    val password: String,
)
