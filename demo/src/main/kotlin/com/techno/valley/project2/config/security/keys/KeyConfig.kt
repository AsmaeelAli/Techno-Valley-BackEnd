package com.techno.valley.project2.config.security.keys

import com.techno.valley.project2.config.security.model.KeyConfigProp
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import java.io.InputStream

@Configuration
class KeyConfig(
    keyConfigProp: KeyConfigProp,
) {

    private val defaultKeyStore = DefaultKeyStore(
        privateKeyInputStream = getInputStream(keyConfigProp.privateKey),
        publicKeyInputStream = getInputStream(keyConfigProp.publicKey),
    )

    @Bean
    fun publicKeyStore(): PublicKeyStore = defaultKeyStore

    @Bean
    fun privateKeyStore(): PrivateKeyStore = defaultKeyStore

    private fun getInputStream(resourcePath: String): InputStream {
        val resource: Resource = if (resourcePath.startsWith("classpath:")) {
            ClassPathResource(resourcePath.removePrefix("classpath:"))
        } else {
            ClassPathResource(resourcePath)
        }
        return resource.inputStream
    }
}
