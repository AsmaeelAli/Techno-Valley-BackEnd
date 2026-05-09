package com.techno.valley.project2.feature.profilePic.entity

import com.techno.valley.project2.utily.ID
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "user_profile_pic")
data class ProfilePicEntity(
    @Id
    var id: ID,
    var userId: ID,
    var imageUrl: String,
)
