package com.techno.valley.project2.feature.expert.data

import com.techno.valley.project2.feature.expert.model.entity.ExpertEntity
import com.techno.valley.project2.utily.ID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ExpertRepo : JpaRepository<ExpertEntity, ID> {
    fun findAllByUserId(userId: ID): List<ExpertEntity>
    fun findByUserId(userId: ID): ExpertEntity?
    fun findByExperienceContainingIgnoreCase(text: String): List<ExpertEntity>
}