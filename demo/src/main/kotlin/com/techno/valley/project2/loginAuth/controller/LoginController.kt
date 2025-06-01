package com.techno.valley.project2.loginAuth.controller

import com.techno.valley.project2.loginAuth.functions.UserLoginFun
import com.techno.valley.project2.loginAuth.model.dto.AuthReqDto
import com.techno.valley.project2.loginAuth.model.dto.AuthRespDto
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class LoginController(
    private val userLoginFun: UserLoginFun,
) {

    @PostMapping("/login")
    fun loginUser(@Valid @RequestBody user: AuthReqDto): AuthRespDto = userLoginFun(user)
}
