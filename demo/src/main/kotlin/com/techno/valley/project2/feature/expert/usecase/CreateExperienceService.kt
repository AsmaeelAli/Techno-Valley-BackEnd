package com.techno.valley.project2.feature.expert.usecase

import com.mxninja.snowflake.Snowflake
import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.expert.data.ExpertRepo
import com.techno.valley.project2.feature.expert.model.entity.ExpertEntity
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class CreateExperienceService(
    private val expertRepo: ExpertRepo,
    private val snowflake: Snowflake,
) {
    @Transactional
    operator fun invoke(auth: UsersAuthentication, experience: String): ExpertEntity {
        val expert = ExpertEntity(
            id = snowflake.nextId(), // أو snowflake.nextId() حسب ما تستخدم
            userId = auth.id,
            experience = experience,
        )
        return expertRepo.save(expert)
    }
}