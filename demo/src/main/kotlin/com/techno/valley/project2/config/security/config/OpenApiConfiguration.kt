package com.techno.valley.project2.config.security.config

import com.techno.valley.project2.config.security.swagger.SwaggerProperties
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class OpenApiConfiguration {

    companion object {
        const val SECURITY_REQUIREMENT_NAME = "Bearer Authentication"
    }

    @Bean
    fun customOpenAPI(properties: SwaggerProperties): OpenAPI {
        return OpenAPI()
            .components(
                Components().addSecuritySchemes(SECURITY_REQUIREMENT_NAME, apiKeySecuritySchema()),
            )
            .info(
                Info().title(properties.title).version(properties.version)
                    .description(properties.description)
                    .termsOfService(properties.termsPath)
                    .contact(Contact().email(properties.email).name(properties.name).url(properties.url))
                    .license(License().name(properties.licenceType).url(properties.licencePath)),
            )
    }

    fun apiKeySecuritySchema(): SecurityScheme = SecurityScheme()
        .name(SECURITY_REQUIREMENT_NAME)
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT")
}
