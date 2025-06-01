package com.techno.valley.project2.feature.users.usecase

import org.springframework.stereotype.Component

@Component
class EmailValidator {
    operator fun invoke(email: String): Boolean {
        val regex = when {
            email.matches("^[a-zA-Z0-9._%+-]+@mutah\\.edu\\.jo$".toRegex()) -> true
            email.matches("^[a-zA-Z0-9._%+-]+@(jadara\\.edu\\.jo|std\\.jadara\\.edu\\.jo)$".toRegex()) -> true
            email.matches("^[a-zA-Z0-9._%+-]+@(hu\\.edu\\.jo|std\\.hu\\.edu\\.jo)$".toRegex()) -> true
            // أضف المزيد من الجامعات هنا
            else -> false
        }
        return regex
    }
}