package com.techno.valley.project2.feature.users.usecase

import org.springframework.stereotype.Component

@Component
class VerificationCodeGenerator {
    operator fun invoke(): String = (1000000..9999999).random().toString()
}
