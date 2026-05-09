package com.techno.valley.project2.feature.saves.data

import com.techno.valley.project2.feature.saves.model.entity.SavesEntity
import com.techno.valley.project2.utily.ID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SavesRepo : JpaRepository<SavesEntity, ID> {
    fun findByUserIdAndPostId(userId: ID, postId: UUID): SavesEntity?

    fun existsByUserIdAndPostIdAndEnableTrue(userId: ID, postId: UUID): Boolean

    fun findByUserIdAndEnableTrue(userId: ID): List<SavesEntity>
}