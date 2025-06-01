package com.techno.valley.project2.config.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
class AppSecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun filterChain(
        http: HttpSecurity,
    ): SecurityFilterChain =
        http.csrf {
            it.disable()
        }.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }.build()

    @Bean
    fun webSecurityConfig(): WebSecurityCustomizer = WebSecurityCustomizer {
        it.ignoring()
            .requestMatchers(AntPathRequestMatcher("/auth/**"))
            .requestMatchers(AntPathRequestMatcher("/v3/api-docs/**"))
            .requestMatchers(AntPathRequestMatcher("configuration/**"))
            .requestMatchers(AntPathRequestMatcher("/swagger*/**"))
            .requestMatchers(AntPathRequestMatcher("/webjars/**"))
            .requestMatchers(AntPathRequestMatcher("/swagger-ui/**"))
    }
}
