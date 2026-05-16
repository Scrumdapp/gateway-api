package com.scrumdapp.gateway.security.jwt

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder

@Configuration
class JwtConfig(
    @Value($$"${RSA_PRIVATE_KEY}") private val privateKey: String,
    @Value($$"${RSA_PUBLIC_KEY}") private val publicKey: String,
    private val jwtKeyUtils: JwtKeyUtils
) {

    @Profile("dev")
    @Bean
    fun devRsaKey(
        @Value($$"${JWT_SECRET}") jwtSecret: String
    ): RSAKey {
        println("Using development generated RSA key")
        return RSAKeyGenerator(2048).keyID(jwtSecret).generate()
    }

    @Bean
    fun rsaKey(): RSAKey {
        println("Using pre-generated .pem RSA key")

        val privateKey = jwtKeyUtils.loadPrivateKey(privateKey)
        val publicKey = jwtKeyUtils.loadPublicKey(publicKey)
        return RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID("Gateway-key")
            .build()
    }

    @Bean
    fun createAsymmetricJwtEncoder(
        rsaKey: RSAKey
    ): JwtEncoder {
        val jwkSet = JWKSet(rsaKey)
        val jwkSource = ImmutableJWKSet<SecurityContext>(jwkSet)
        return NimbusJwtEncoder(jwkSource)
    }
}