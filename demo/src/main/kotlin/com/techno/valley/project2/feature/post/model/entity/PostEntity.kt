package com.techno.valley.project2.feature.post.model.entity

import com.techno.valley.project2.utily.ID
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "posts_entity")
data class PostEntity(
    @Id
    var id: UUID,
    var userId: ID,
    var content: String,
    var fileUrl: String,
    var imageUrl: String,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)
