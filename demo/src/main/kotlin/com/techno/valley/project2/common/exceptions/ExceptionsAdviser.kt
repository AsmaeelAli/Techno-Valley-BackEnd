package com.techno.valley.project2.common.exceptions

import jakarta.annotation.Priority
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
@Priority(0)
class ExceptionsAdviser {

    @ExceptionHandler(Exception::class)
    fun onUnexpected(ex: Exception): ResponseEntity<DefaultErrorResp> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(DefaultErrorResp(500, ex.localizedMessage ?: "Internal server error", ""))
    }

    @ExceptionHandler(RestExceptions::class)
    fun onCustom(ex: RestExceptions): ResponseEntity<DefaultErrorResp> {
        return ResponseEntity.status(ex.status)
            .body(DefaultErrorResp(ex.status.value(), ex.errorMessage, ""))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun onValidation(ex: MethodArgumentNotValidException): ResponseEntity<DefaultErrorResp> {
        val error = ex.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "Invalid input"
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(DefaultErrorResp(400, error, ""))
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun onMethodNotAllowed(ex: HttpRequestMethodNotSupportedException): ResponseEntity<DefaultErrorResp> {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .body(DefaultErrorResp(405, "Method not allowed", ""))
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun onMissingParam(ex: MissingServletRequestParameterException): ResponseEntity<DefaultErrorResp> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(DefaultErrorResp(400, "Missing parameter: ${ex.parameterName}", ""))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun onUnreadableJson(ex: HttpMessageNotReadableException): ResponseEntity<DefaultErrorResp> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(DefaultErrorResp(400, "Malformed JSON request", ""))
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun onAccessDenied(ex: AccessDeniedException): ResponseEntity<DefaultErrorResp> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(DefaultErrorResp(403, "Access denied", ""))
    }

    @ExceptionHandler(org.springframework.security.core.AuthenticationException::class)
    fun onAuthError(ex: org.springframework.security.core.AuthenticationException): ResponseEntity<DefaultErrorResp> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(DefaultErrorResp(401, "Unauthorized: ${ex.localizedMessage}", ""))
    }
}
