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
    var name: String,
    var password: String,
    var email: String,
    var verificationCode: String,
    var enable: Boolean,
    var createdAt: LocalDateTime,
    var aboutMe: String,
)
