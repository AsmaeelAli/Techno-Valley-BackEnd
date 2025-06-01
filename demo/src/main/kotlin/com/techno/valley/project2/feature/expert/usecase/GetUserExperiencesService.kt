package com.techno.valley.project2.feature.expert.usecase

import com.techno.valley.project2.common.exceptions.RestExceptions
import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.expert.data.ExpertRepo
import org.springframework.stereotype.Service

@Service
class GetUserExperiencesService(
    private val expertRepo: ExpertRepo
) {
    operator fun invoke(auth: UsersAuthentication): String {
        return expertRepo.findByUserId(auth.id)?.experience
            ?: throw RestExceptions.NotFound("No experience found")
    }
}