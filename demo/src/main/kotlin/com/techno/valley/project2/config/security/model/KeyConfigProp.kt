package com.techno.valley.project2.config.security.model

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security.key")
data class KeyConfigProp(
    val privateKey: String,
    val publicKey: String,
)