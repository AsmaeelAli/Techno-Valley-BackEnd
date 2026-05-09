package com.techno.valley.project2.feature.hashtags.usecase

import com.mxninja.snowflake.Snowflake
import com.techno.valley.project2.feature.hashtags.data.PostHashtagRepo
import com.techno.valley.project2.feature.hashtags.model.entity.PostHashtagEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class SaveHashtagsService(
    private val hashtagRepo: PostHashtagRepo,
    private val snowflake: Snowflake,
) {
    operator fun invoke(postId: UUID, tags: List<String>) {
        if (tags.isEmpty()) return

        val entities = tags.distinct().map { tag ->
            PostHashtagEntity(
                id = snowflake.nextId(),
                postId = postId,
                tag = tag.trim().lowercase() // normalize
            )
        }

        hashtagRepo.saveAll(entities)
    }
}