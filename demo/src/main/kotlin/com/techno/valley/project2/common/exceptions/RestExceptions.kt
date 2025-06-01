package com.techno.valley.project2.common.exceptions

import org.springframework.http.HttpStatus

sealed class RestExceptions(
    val status: HttpStatus,
    val errorMessage: String,
) : RuntimeException(errorMessage) {

    class BadRequest(error: String) : RestExceptions(HttpStatus.BAD_REQUEST, error)
    class NotFound(error: String) : RestExceptions(HttpStatus.NOT_FOUND, error)
    class Unauthorized(error: String) : RestExceptions(HttpStatus.UNAUTHORIZED, error)
    class Forbidden(error: String) : RestExceptions(HttpStatus.FORBIDDEN, error)
    class Conflict(error: String) : RestExceptions(HttpStatus.CONFLICT, error)
}

