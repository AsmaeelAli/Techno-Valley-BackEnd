package com.techno.valley.project2.feature.users.controller

import com.techno.valley.project2.feature.users.model.dto.CodeVerificationReq
import com.techno.valley.project2.feature.users.model.dto.EmailDto
import com.techno.valley.project2.feature.users.model.dto.UsersDto
import com.techno.valley.project2.feature.users.usecase.EmailService
import com.techno.valley.project2.feature.users.usecase.ResendCodeService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/email")
class UsersController(
    private val emailService: EmailService,
    private val resendCodeService: ResendCodeService,
) {
    @PostMapping("/request")
    fun requestVerification(@RequestBody request: UsersDto): String {
        return emailService.sendEmail(
            name = request.name,
            email = request.email,
            password = request.password,
        )
    }

    @PostMapping("/verify")
    fun verifyCode(@RequestBody request: CodeVerificationReq): Map<String, Boolean> {
        val success = emailService.verifyCode(request.email, request.code)
        return mapOf("success" to success)
    }

    @PostMapping("/resend")
    fun resendCode(@RequestBody email: EmailDto): String = resendCodeService.resendCode(email)

}
