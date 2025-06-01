package com.techno.valley.project2.feature.expert.model.entity

import com.techno.valley.project2.utily.ID
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "user_experiences")
data class ExpertEntity(
    @Id
    val id: ID,
    val userId: ID,
    val experience: String,
)
