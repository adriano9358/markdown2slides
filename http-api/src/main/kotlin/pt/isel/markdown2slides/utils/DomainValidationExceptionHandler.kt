package pt.isel.markdown2slides.utils

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import pt.isel.markdown2slides.model.Problem

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(DomainValidationException::class)
    fun handleValidationException(ex: DomainValidationException): ResponseEntity<Any> {
        return Problem.ValidationFailure(ex.errors).response()
    }
}

class DomainValidationException(val errors: List<String>) : RuntimeException()
