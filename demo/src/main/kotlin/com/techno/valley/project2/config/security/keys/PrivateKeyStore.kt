package com.techno.valley.project2.config.security.keys

import java.security.PrivateKey

interface PrivateKeyStore {

    fun getPrivateKey(): PrivateKey
}