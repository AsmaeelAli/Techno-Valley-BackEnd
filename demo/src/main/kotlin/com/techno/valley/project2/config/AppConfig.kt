package com.techno.valley.project2.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.mxninja.snowflake.Snowflake
import com.techno.valley.project2.utily.ID
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class AppConfig {
    @Bean
    fun snowflakeIdGenerator() = Snowflake(1)

    @Bean
    @Qualifier("mainObjectMapper")
    @Primary
    fun createMapper(): ObjectMapper {
        val builder = Jackson2ObjectMapperBuilder()
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        return builder.build<ObjectMapper>()
            .registerKotlinModule()
            .registerModule(
                SimpleModule()
                    .addSerializer(ID::class.javaObjectType, IdJsonSerializer())
                    .addSerializer(ID::class.javaPrimitiveType, IdJsonSerializer())
                    .addDeserializer(ID::class.javaObjectType, IdJsonDeserializer())
                    .addDeserializer(ID::class.javaPrimitiveType, IdJsonDeserializer()),
            )
    }
}
