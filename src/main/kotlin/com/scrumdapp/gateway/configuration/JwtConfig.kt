package com.scrumdapp.gateway.configuration

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder

@Configuration
class JwtConfig {

    @Bean
    fun rsaKey(
        @Value($$"${JWT_SECRET}") jwtSecret: String
    ):RSAKey {
        return RSAKeyGenerator(2048).keyID(jwtSecret).generate()
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