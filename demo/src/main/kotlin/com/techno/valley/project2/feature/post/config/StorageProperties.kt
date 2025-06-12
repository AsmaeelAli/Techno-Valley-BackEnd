package com.techno.valley.project2.feature.post.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "storage")
class StorageProperties {
    lateinit var location: String
    lateinit var tempUploadDir: String
    lateinit var quarantineDir: String
    lateinit var suspiciousLogPath: String
}
