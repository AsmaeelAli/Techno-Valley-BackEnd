package com.techno.valley.project2.feature.likes.usecase

import com.mxninja.snowflake.Snowflake
import com.techno.valley.project2.feature.likes.data.LikesRepo
import com.techno.valley.project2.feature.likes.model.entity.LikesEntity
import com.techno.valley.project2.utily.ID
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*


@Service
class CreateLike(
    private val likesRepo: LikesRepo,
    private val snowflake: Snowflake
) {

    operator fun invoke(userId: ID, postId: UUID): LikesEntity {
        val newLike = LikesEntity(
            id = snowflake.nextId(),
            userId = userId,
            postId = postId,
            enable = true,
            createAt = LocalDateTime.now()
        )
        return likesRepo.save(newLike)
    }
}
