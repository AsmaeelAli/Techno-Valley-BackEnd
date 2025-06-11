package com.techno.valley.project2.feature.profilePic.data

import com.techno.valley.project2.feature.profilePic.entity.ProfilePicEntity
import com.techno.valley.project2.utily.ID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProfilePicRepo : JpaRepository<ProfilePicEntity, ID>