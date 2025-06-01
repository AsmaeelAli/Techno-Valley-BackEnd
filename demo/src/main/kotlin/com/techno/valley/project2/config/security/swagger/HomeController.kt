package com.techno.valley.project2.config.security.swagger

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

private const val HOME_REDIRECT = "redirect:/swagger-ui/index.html"
private const val DOCS_REDIRECT = "redirect:/v3/api-docs"

@Controller
class HomeController {

    @GetMapping("/")
    fun home(): String = HOME_REDIRECT

    @GetMapping("/swagger-docs")
    fun swaggerDocs(): String = DOCS_REDIRECT
}
