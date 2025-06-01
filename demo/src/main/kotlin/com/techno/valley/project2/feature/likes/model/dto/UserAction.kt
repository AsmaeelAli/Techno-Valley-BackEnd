package com.techno.valley.project2.feature.likes.model.dto

import java.time.LocalDateTime

data class UserAction(
    val like: Int,
    val save: Int,
    val blockUntil: LocalDateTime
)