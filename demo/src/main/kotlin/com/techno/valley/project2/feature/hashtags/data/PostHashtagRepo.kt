package com.techno.valley.project2.feature.hashtags.data

import com.techno.valley.project2.feature.hashtags.model.entity.PostHashtagEntity
import com.techno.valley.project2.utily.ID
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PostHashtagRepo : JpaRepository<PostHashtagEntity, ID> {

    fun findByTagStartingWithIgnoreCase(tag: String): List<PostHashtagEntity>

    fun findByPostId(id: UUID): PostHashtagEntity
}
