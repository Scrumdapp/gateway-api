package com.scrumdapp.gateway.utils

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

@Component
class KeyUtils(
) {

    @Autowired lateinit var resourceLoader: ResourceLoader

    public fun loadPrivateKey(path: String): RSAPrivateKey {
        val key = readKeyFromFile(path)
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s+".toRegex(), "")

        val decodedKey = Base64.getDecoder().decode(key)
        val keySpec = PKCS8EncodedKeySpec(decodedKey)
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec) as RSAPrivateKey
    }

    public fun loadPublicKey(path: String): RSAPublicKey {
        val key = readKeyFromFile(path)
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s+".toRegex(), "")

        val decodedKey = Base64.getDecoder().decode(key)
        val keySpec = X509EncodedKeySpec(decodedKey)
        return KeyFactory.getInstance("RSA").generatePublic(keySpec) as RSAPublicKey
    }

    private fun readKeyFromFile(path: String): String {
        val input = resourceLoader.getResource(path) ?: throw IllegalArgumentException("Key not found at path: $path")
        return String(input.inputStream.readBytes())
    }
}