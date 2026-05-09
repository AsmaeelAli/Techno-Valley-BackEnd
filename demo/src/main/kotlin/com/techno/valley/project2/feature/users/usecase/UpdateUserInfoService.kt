package com.techno.valley.project2.feature.users.usecase

import com.techno.valley.project2.common.exceptions.RestExceptions
import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.users.data.UsersRepo
import com.techno.valley.project2.feature.users.model.dto.AboutMeRequest
import org.springframework.stereotype.Service

@Service
class UpdateUserInfoService(
    private val usersRepo: UsersRepo,
) {
    operator fun invoke(auth: UsersAuthentication, info: AboutMeRequest): String {
        val user = usersRepo.findById(auth.id).orElseThrow {
            RestExceptions.Forbidden("User not found")
        }

        user.aboutMe = info.aboutMe
        usersRepo.save(user)
        return user.aboutMe
    }
}
