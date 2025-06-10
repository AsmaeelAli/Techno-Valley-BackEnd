package com.techno.valley.project2.feature.likes.data

import com.techno.valley.project2.feature.likes.model.entity.LikesEntity
import com.techno.valley.project2.utily.ID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface LikesRepo: JpaRepository<LikesEntity, ID>{

    fun existsByUserIdAndPostIdAndEnableTrue(userId: ID , postId: UUID): Boolean

    // البحث عن الإعجابات بناءً على userId
    fun findByUserIdAndEnableTrue(userId: ID): List<LikesEntity>

    // البحث عن إعجاب محدد بناءً على userId و postId
    fun findByUserIdAndPostIdAndEnableTrue(userId: ID, postId: UUID): LikesEntity?
}