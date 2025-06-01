package com.techno.valley.project2.feature.post.model.dto

import java.time.LocalDateTime
import java.util.UUID

data class PostResponse(
    val id: UUID,
    val userId: Long,
    val content: String,
    val fileUrl: String?,
    val imageUrl: String?,
    val createdAt: LocalDateTime,
)
