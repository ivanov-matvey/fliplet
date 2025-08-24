package dev.matvenoid.backend.application.advice

import dev.matvenoid.backend.domain.exception.UserAlreadyExistsException
import dev.matvenoid.backend.domain.exception.UserNotFoundException
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.security.SecurityException as JwtSecurityException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import kotlin.collections.mapNotNull
import kotlin.collections.toMap
import kotlin.let
import kotlin.to

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ProblemDetail {
        val errors = e.bindingResult
            .allErrors
            .mapNotNull {
                (it as? FieldError)?.let {
                    error -> error.field to (error.defaultMessage ?: "Invalid value")
                }
            }
            .toMap()

        return ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Одно или несколько полей не прошли валидацию"
        ).apply {
            title = "Validation Failed"
            setProperty("errors", errors)
        }
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExistsException(e: UserAlreadyExistsException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            e.message ?: "Пользователь с такими данными уже существует"
        ).apply { title = "User Already Exists" }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(e: UserNotFoundException): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            e.message ?: "Пользователь не найден"
        ).apply { title = "User Not Found" }

    @ExceptionHandler(
        BadCredentialsException::class,
        UsernameNotFoundException::class
    )
    fun handleAuthenticationException(e: Exception): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            e.message ?: "Неверный адрес электронной почты или пароль"
        ).apply { title = "Authentication Failed" }

    @ExceptionHandler(
        JwtSecurityException::class,
        JwtException::class
    )
    fun handleTokenException(e: Exception): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            e.message ?: "Сессия истекла или токен недействителен"
        ).apply { title = "Invalid Token" }

    @ExceptionHandler(Exception::class)
    fun handleUnexpectedException(e: Exception): ProblemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            e.message ?: "Произошла непредвиденная ошибка на сервере"
        ).apply { title = "Internal Server Error" }
}
