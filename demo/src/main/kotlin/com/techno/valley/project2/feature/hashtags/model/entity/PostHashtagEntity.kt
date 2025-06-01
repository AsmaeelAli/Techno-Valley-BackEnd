package com.techno.valley.project2.feature.hashtags.model.entity

import com.techno.valley.project2.utily.ID
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.util.UUID

@Entity
@Table(
    name = "post_hashtags",
    uniqueConstraints = [UniqueConstraint(columnNames = ["post_id", "tag"])]
)
data class PostHashtagEntity(
    @Id
    val id: ID,

    @Column(name = "post_id", nullable = false)
    val postId: UUID,

    @Column(nullable = false, length = 50)
    val tag: String
)