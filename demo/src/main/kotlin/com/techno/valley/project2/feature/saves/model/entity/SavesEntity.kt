package com.techno.valley.project2.feature.saves.model.entity

import com.techno.valley.project2.utily.ID
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.*

@Table(name = "favorites")
@Entity
data class SavesEntity(
    @Id
    var id: ID,
    var userId: ID,
    var postId: UUID,
    var enable: Boolean,
    var createAt: LocalDateTime,
)