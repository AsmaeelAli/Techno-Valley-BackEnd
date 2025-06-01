package com.techno.valley.project2.feature.users.model.dto

data class CodeVerificationReq(
    val email: String,
    val code: String,
)
