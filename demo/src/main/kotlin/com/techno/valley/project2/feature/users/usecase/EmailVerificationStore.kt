package com.techno.valley.project2.feature.users.usecase

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class EmailVerificationStore {
    val verificationCodes = mutableMapOf<String, Triple<String, String, String>>()
    val codeExpiry = mutableMapOf<String, LocalDateTime>()
    val requestedEmails = mutableSetOf<String>()

    val sendAttempts = mutableMapOf<String, Int>()
}
