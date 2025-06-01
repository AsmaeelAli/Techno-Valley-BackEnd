package com.techno.valley.project2.feature.users.model.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users_entity")
data class UsersEntity(
    @Id
    val id: Long,
    val name: String,
    val password: String,
    val email: String,
    val verificationCode: String,
    val createdAt: LocalDateTime,
)
