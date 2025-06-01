package com.techno.valley.project2.config.security.keys

import java.io.InputStream
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

class DefaultKeyStore(
    privateKeyInputStream: InputStream,
    publicKeyInputStream: InputStream,
) : PrivateKeyStore, PublicKeyStore {

    private val privateKey = privateKeyInputStream.readContent()
        .replace("\\n".toRegex(), "")
        .replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .let {
            PKCS8EncodedKeySpec(Base64.getDecoder().decode(it))
        }.let {
            KeyFactory.getInstance("RSA").generatePrivate(it)
        }!!

    private val publicKey = publicKeyInputStream.readContent()
        .replace("\\n".toRegex(), "")
        .replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .let {
            X509EncodedKeySpec(Base64.getDecoder().decode(it))
        }.let {
            KeyFactory.getInstance("RSA").generatePublic(it)
        }!!

    override fun getPrivateKey(): PrivateKey = privateKey

    override fun getPublicKey(): PublicKey = publicKey

    private fun InputStream.readContent() = this.bufferedReader().use { it.readText() }
}
