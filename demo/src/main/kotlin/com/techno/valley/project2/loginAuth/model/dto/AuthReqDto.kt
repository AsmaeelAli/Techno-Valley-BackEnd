package com.techno.valley.project2.loginAuth.model.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size


data class AuthReqDto(
    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email must not be blank")
    val email: String,

    @field:NotBlank(message = "Password must not be blank")
    @field:Size(min = 5, max = 32, message = "Password incorrect")
    val password: String,
)