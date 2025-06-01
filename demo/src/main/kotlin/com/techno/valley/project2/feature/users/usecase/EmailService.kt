package com.techno.valley.project2.feature.users.usecase

import com.techno.valley.project2.common.exceptions.RestExceptions
import com.techno.valley.project2.feature.users.data.UsersRepo
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class EmailService(
    private val usersRepo: UsersRepo,
    private val emailValidator: EmailValidator,
    private val codeGenerator: VerificationCodeGenerator,
    private val emailSender: EmailSender,
    private val verificationHandler: VerificationHandler,
    private val store: EmailVerificationStore,
) {
    fun sendEmail(name: String, email: String, password: String): String {
        if (!emailValidator(email)) throw RestExceptions.BadRequest("Invalid email format")

        if (usersRepo.existsByEmail(email)) throw RestExceptions.BadRequest("Email already exists")

        val attempts = store.sendAttempts.getOrDefault(email, 0)

        if (attempts >= 3) {
            throw RestExceptions.BadRequest("You have exceeded the allowed number of verification attempts.")
        }

        val code = codeGenerator()
        store.verificationCodes[email] = Triple(code, name, password)
        store.codeExpiry[email] = LocalDateTime.now()

        val result = emailSender(name, email, code)

        return when (result) {
            "sent" -> {
                store.sendAttempts[email] = attempts + 1 // ✅ تصحيح هنا
                store.requestedEmails.add(email)
                println(store.requestedEmails)
                "Verification code sent!"
            }

            else -> result
        }
    }

    fun verifyCode(email: String, inputCode: String): Boolean {
        val stored = store.verificationCodes[email] ?: return false
        val expiry = store.codeExpiry[email] ?: return false

        val result = verificationHandler(email, inputCode, stored, expiry)

        return if (result == "Email verified and account created!") {
            store.verificationCodes.remove(email)
            store.codeExpiry.remove(email)
            store.requestedEmails.remove(email)
            true
        } else {
            false
        }
    }
}
