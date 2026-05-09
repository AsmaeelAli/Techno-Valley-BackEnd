package com.techno.valley.project2.feature.users.controller

import com.techno.valley.project2.config.security.model.UsersAuthentication
import com.techno.valley.project2.feature.users.model.dto.AboutMeRequest
import com.techno.valley.project2.feature.users.model.dto.CodeVerificationReq
import com.techno.valley.project2.feature.users.model.dto.EmailDto
import com.techno.valley.project2.feature.users.model.dto.UsersDto
import com.techno.valley.project2.feature.users.usecase.EmailService
import com.techno.valley.project2.feature.users.usecase.GetUserInfoService
import com.techno.valley.project2.feature.users.usecase.ResendCodeService
import com.techno.valley.project2.feature.users.usecase.UpdateUserInfoService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/email")
class UsersController(
    private val emailService: EmailService,
    private val resendCodeService: ResendCodeService,
    private val updateUserInfoService: UpdateUserInfoService,
    private val getUserInfoService: GetUserInfoService,
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

    @PutMapping("/update")
    @PreAuthorize("hasRole('USER')")
    fun updateInfo(auth: UsersAuthentication, @RequestBody aboutMe: AboutMeRequest): String =
        updateUserInfoService(auth, aboutMe)

    @GetMapping("/get/info")
    @PreAuthorize("hasRole('USER')")
    fun getInfo(auth: UsersAuthentication): String = getUserInfoService(auth)
}
