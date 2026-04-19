package com.scrumdapp.gateway.configuration

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.SecurityContext
import com.scrumdapp.gateway.utils.KeyUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import java.security.interfaces.RSAPrivateKey

@Configuration
class JwtConfig(
    @Value($$"${jwt.private-key-path:classpath:keys/private.pem}") private val privateKeyPath: String,
    @Value($$"${jwt.public-key-path:classpath:keys/public.pem}") private val publicKeyPath: String,
    private val keyUtils: KeyUtils
) {

    @Profile("dev")
    @Bean
    fun devRsaKey(
        @Value($$"${JWT_SECRET}") jwtSecret: String
    ):RSAKey {
        return RSAKeyGenerator(2048).keyID(jwtSecret).generate()
    }

    @Bean
    fun rsaKey():RSAKey {
        val privateKey = keyUtils.loadPrivateKey(privateKeyPath)
        val publicKey = keyUtils.loadPublicKey(publicKeyPath)

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