package com.techno.valley.project2.loginAuth.functions

import org.springframework.stereotype.Service

@Service
class UniversityResolver {
    operator fun invoke (email: String): String {
        val domain = email.substringAfter("@")
        return when (domain) {
            "mutah.edu.jo" -> "Mutah University"
            "hu.edu.jo" -> "Hashemite University"
            "std.hu.edu.jo" -> "Hashemite University"
            "ju.edu.jo" -> "Jordan University"
            "jadara.edu.jo" -> "Jadara University"
            "std.jadara.edu.jo" -> "Jadara University"
            else -> "Unknown University"
        }
    }
}
