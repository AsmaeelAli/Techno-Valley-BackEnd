package com.techno.valley.project2.common.exceptions

data class DefaultErrorResp(
    val code: Int,
    val message: String,
    val internalCode: String,
)
