package com.techno.valley.project2.feature.saves.usecase

import com.mxninja.snowflake.Snowflake
import com.techno.valley.project2.feature.saves.data.SavesRepo
import com.techno.valley.project2.feature.saves.model.entity.SavesEntity
import com.techno.valley.project2.utily.ID
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class CreatSave(
    private val savesRepo: SavesRepo,
    private val snowflake: Snowflake,
) {

    operator fun invoke(userId: ID, postId: UUID): SavesEntity {
        val newSave = SavesEntity(
            id = snowflake.nextId(),
            userId = userId,
            postId = postId,
            enable = true,
            createAt = LocalDateTime.now()
        )
        return savesRepo.save(newSave)
    }
}