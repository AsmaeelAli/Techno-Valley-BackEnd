package com.techno.valley.project2

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity


@SpringBootApplication
@ConfigurationPropertiesScan
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class TechnoValleyApplication

fun main(args: Array<String>) {
    runApplication<TechnoValleyApplication>(*args)
}
