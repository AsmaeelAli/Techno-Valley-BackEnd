package com.techno.valley.project2.feature.post.data

import com.techno.valley.project2.feature.post.model.entity.PostEntity
import com.techno.valley.project2.utily.ID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PostRepository : JpaRepository<PostEntity, UUID>{
    fun findByUserId(userId: ID): List<PostEntity>
}



