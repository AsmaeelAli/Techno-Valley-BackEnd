package com.techno.valley.project2.feature.users.usecase

import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.users.data.UsersRepo
import org.springframework.stereotype.Service

@Service
class GetUserInfoService(
    private val usersRepo: UsersRepo
) {
    operator fun invoke(auth: UsersAuthentication): String = usersRepo.findById(auth.id).get().aboutMe
}