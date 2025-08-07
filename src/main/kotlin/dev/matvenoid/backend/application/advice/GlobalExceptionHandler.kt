package dev.matvenoid.backend.application.advice

import dev.matvenoid.backend.domain.exception.InvalidTokenException
import dev.matvenoid.backend.domain.exception.UserAlreadyExistsException
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
    fun handleValidation(ex: MethodArgumentNotValidException): ProblemDetail {
        val errors = ex.bindingResult.allErrors
            .mapNotNull { error ->
                (error as? FieldError)?.let { it.field to (it.defaultMessage ?: "Invalid value") }
            }
            .toMap()

        val problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
        problemDetail.title = "Validation Failed"
        problemDetail.detail = "Одно или несколько полей не прошли валидацию"
        problemDetail.setProperty("errors", errors)

        return problemDetail
    }

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExists(ex: UserAlreadyExistsException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            ex.message ?: "Пользователь с такими данными уже существует"
        )
        problemDetail.title = "User Already Exists"
        return problemDetail
    }

    @ExceptionHandler(BadCredentialsException::class, UsernameNotFoundException::class)
    fun handleAuthenticationFailure(e: Exception): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            "Неверный номер телефона или пароль"
        )
        problemDetail.title = "Authentication Failed"
        return problemDetail
    }

    @ExceptionHandler(InvalidTokenException::class, JwtSecurityException::class)
    fun handleTokenErrors(e: Exception): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            "Сессия истекла или токен недействителен"
        )
        problemDetail.title = "Invalid Token"
        return problemDetail
    }

    @ExceptionHandler(Exception::class)
    fun handleAllUncaughtException(e: Exception): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Произошла непредвиденная ошибка на сервере"
        )
        problemDetail.title = "Internal Server Error"
        return problemDetail
    }
}
