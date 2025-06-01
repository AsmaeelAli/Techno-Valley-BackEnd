package com.techno.valley.project2.config.security.keys

import java.security.PublicKey

interface PublicKeyStore {

    fun getPublicKey(): PublicKey
}