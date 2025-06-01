package com.techno.valley.project2.config.security.filter

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus

object FilterUtils {

    fun respondWithError(
        response: HttpServletResponse,
        status: HttpStatus,
        message: String,
        details: String = ""
    ) {
        response.status = status.value()
        response.contentType = "application/json"

        val errorBody = """
            {
                "code": ${status.value()},
                "message": "${message.replace("\"", "\\\"")}",
                "details": "${details.replace("\"", "\\\"")}"
            }
        """.trimIndent()

        response.writer.write(errorBody)
    }
}