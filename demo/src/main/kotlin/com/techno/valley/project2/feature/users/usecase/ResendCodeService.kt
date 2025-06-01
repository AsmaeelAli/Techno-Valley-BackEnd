package com.techno.valley.project2.feature.users.usecase

import com.techno.valley.project2.common.exceptions.RestExceptions
import com.techno.valley.project2.feature.users.model.dto.EmailDto
import com.techno.valley.project2.utily.cleaned
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ResendCodeService(
    private val emailSender: EmailSender,
    private val codeGenerator: VerificationCodeGenerator,
    private val store: EmailVerificationStore,
) {
    private val resendAttempts = mutableMapOf<String, Int>()

    fun resendCode(emailDto: EmailDto): String {
        val email = emailDto.cleaned()

        if (!store.requestedEmails.contains(email)) {
            throw RestExceptions.BadRequest("No previous verification request found for this email.")
        }

        val stored = store.verificationCodes[email]
            ?: throw RestExceptions.BadRequest("No registration attempt found for this email.")

        val attempts = resendAttempts.getOrDefault(email, 0)
        if (attempts >= 2) {
            throw RestExceptions.BadRequest("You have reached the maximum number of resend attempts.")
        }

        val (_, name, password) = stored
        val newCode = codeGenerator()

        store.verificationCodes[email] = Triple(newCode, name, password)
        store.codeExpiry[email] = LocalDateTime.now()

        val result = emailSender(name, email, newCode)

        return when (result) {
            "sent" -> {
                resendAttempts[email] = attempts + 1
                store.requestedEmails.add(email)
                "Verification code resent successfully!"
            }

            else -> "Failed to resend: $result"
        }
    }
}
