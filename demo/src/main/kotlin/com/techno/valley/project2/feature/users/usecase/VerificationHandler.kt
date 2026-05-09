package com.techno.valley.project2.feature.users.usecase

import com.mxninja.snowflake.Snowflake
import com.techno.valley.project2.feature.users.data.UsersRepo
import com.techno.valley.project2.feature.users.model.entity.UsersEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class VerificationHandler(
    private val usersRepo: UsersRepo,
    private val passwordEncoder: PasswordEncoder,
    private val snowflake: Snowflake,
    private val store: EmailVerificationStore,
) {
    operator fun invoke(
        email: String,
        inputCode: String,
        stored: Triple<String, String, String>,
        expiry: LocalDateTime,
    ): String {
        val (code, name, rawPassword) = stored

        if (code != inputCode) return "Wrong code!"

        if (expiry.plusMinutes(3).isBefore(LocalDateTime.now())) return "Code expired!"

        val id = snowflake.nextId()
        val hashedPassword = passwordEncoder.encode(rawPassword)



        usersRepo.save(
            UsersEntity(
                id = id,
                name = name,
                email = email,
                password = hashedPassword,
                verificationCode = code,
                enable = true,
                createdAt = LocalDateTime.now(),
                aboutMe = "",
            ),
        )

        store.sendAttempts.remove(email)

        return "Email verified and account created!"
    }
}
