package com.techno.valley.project2.loginAuth.functions

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class LoginAttemptService {
    private val attempts = ConcurrentHashMap<String, Int>()
    private val MAX_ATTEMPTS = 5

    fun loginFailed(identifier: String) {
        val count = attempts.getOrDefault(identifier, 0)
        attempts[identifier] = count + 1
    }

    fun loginSucceeded(identifier: String) {
        attempts.remove(identifier)
    }

    fun isBlocked(identifier: String): Boolean {
        return attempts.getOrDefault(identifier, 0) >= MAX_ATTEMPTS
    }
}
