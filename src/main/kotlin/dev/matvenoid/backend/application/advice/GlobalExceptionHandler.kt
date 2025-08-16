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
    fun handleValidation(e: MethodArgumentNotValidException): ProblemDetail {
        val errors = e.bindingResult.allErrors
            .mapNotNull { error ->
                (error as? FieldError)?.let { it.field to (it.defaultMessage ?: "Invalid value") }
            }
            .toMap()

        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Одно или несколько полей не прошли валидацию"
        )
        problemDetail.title = "Validation Failed"
        problemDetail.setProperty("errors", errors)

        return problemDetail
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExists(e: UserAlreadyExistsException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            e.message ?: "Пользователь с такими данными уже существует"
        )
        problemDetail.title = "User Already Exists"
        return problemDetail
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(e: UserNotFoundException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND,
            e.message ?: "Пользователь не найден"
        )
        problemDetail.title = "User not found"
        return problemDetail
    }

    @ExceptionHandler(BadCredentialsException::class, UsernameNotFoundException::class)
    fun handleAuthenticationFailure(e: Exception): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            e.message ?: "Неверный адрес электронной почты или пароль"
        )
        problemDetail.title = "Authentication Failed"
        return problemDetail
    }

    @ExceptionHandler(
        JwtSecurityException::class,
        JwtException::class
    )
    fun handleTokenErrors(e: Exception): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            e.message ?: "Сессия истекла или токен недействителен"
        )
        problemDetail.title = "Invalid Token"
        return problemDetail
    }

    @ExceptionHandler(Exception::class)
    fun handleAllUncaughtException(e: Exception): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            e.message ?: "Произошла непредвиденная ошибка на сервере"
        )
        problemDetail.title = "Internal Server Error"
        return problemDetail
    }
}
