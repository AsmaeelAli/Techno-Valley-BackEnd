package com.techno.valley.project2.config.security.swagger

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "swagger.service")
data class SwaggerProperties(
    val version: String,
    val title: String,
    val description: String,
    val termsPath: String,
    val email: String,
    val name: String,
    val url: String,
    val licenceType: String,
    val licencePath: String,
)
