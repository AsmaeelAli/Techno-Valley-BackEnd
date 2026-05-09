package com.techno.valley.project2.loginAuth.functions

import com.techno.valley.project2.common.exceptions.RestExceptions
import com.techno.valley.project2.config.security.keys.PrivateKeyStore
import com.techno.valley.project2.feature.users.data.UsersRepo
import com.techno.valley.project2.loginAuth.model.dto.AuthReqDto
import com.techno.valley.project2.loginAuth.model.dto.AuthRespDto
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.ZonedDateTime
import java.util.*

@Service
class UserLoginFun(
    private val privateKeyStore: PrivateKeyStore,
    private val passwordEncoder: PasswordEncoder,
    private val usersRepo: UsersRepo,
    private val universityResolver: UniversityResolver,
    private val loginAttemptService: LoginAttemptService,
    @Value("\${security.expInHours}") private val expInHours: Long,
) {
    operator fun invoke(dto: AuthReqDto): AuthRespDto {
        if (loginAttemptService.isBlocked(dto.email)) {
            throw RestExceptions.BadRequest("Too many failed attempts. Try again later.")
        }

        val maybeUser = usersRepo.findByEmailIgnoreCase(dto.email)
        if (maybeUser.isEmpty) {
            loginAttemptService.loginFailed(dto.email)
            throw RestExceptions.BadRequest("incorrect password or email")
        }

        val user = maybeUser.get()
        if (!passwordEncoder.matches(dto.password, user.password)) {
            loginAttemptService.loginFailed(dto.email)
            throw RestExceptions.BadRequest("incorrect password or email")
        }

        loginAttemptService.loginSucceeded(dto.email)

        val university = universityResolver(user.email)

        val now = ZonedDateTime.now()
        val validity = now.plusHours(expInHours)
        val jwt = Jwts.builder()
            .id(user.id.toString())
            .subject(user.name)
            .claims(
                mutableMapOf(
                    "role" to "USER",
                    "email" to user.email,
                    "university" to university,
                ),
            )
            .expiration(Date.from(validity.toInstant()))
            .issuedAt(Date.from(now.toInstant()))
            .signWith(privateKeyStore.getPrivateKey())
            .compact()

        return AuthRespDto("Login successful", jwt)
    }
}
