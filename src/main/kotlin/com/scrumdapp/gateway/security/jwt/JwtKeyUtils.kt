package com.scrumdapp.gateway.security.jwt

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
class JwtKeyUtils(
) {

    @Autowired
    lateinit var resourceLoader: ResourceLoader

    fun loadPrivateKey(key: String): RSAPrivateKey {
        val key = key
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s+".toRegex(), "")

        val decodedKey = Base64.getDecoder().decode(key)
        val keySpec = PKCS8EncodedKeySpec(decodedKey)
        return KeyFactory.getInstance("RSA").generatePrivate(keySpec) as RSAPrivateKey
    }

    fun loadPublicKey(key: String): RSAPublicKey {
        val key = key
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "")
            .replace("\\s+".toRegex(), "")

        val decodedKey = Base64.getDecoder().decode(key)
        val keySpec = X509EncodedKeySpec(decodedKey)
        return KeyFactory.getInstance("RSA").generatePublic(keySpec) as RSAPublicKey
    }
}