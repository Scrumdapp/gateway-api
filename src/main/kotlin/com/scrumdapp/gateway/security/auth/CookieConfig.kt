package com.scrumdapp.gateway.security.auth

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CookieConfig(
    @Value($$"${SESSION_LIFETIME:1209600}") private val sessionAge: Int // default 2 weeks
) {

    @Bean
    fun servletCookieInitializer(): ServletContextInitializer {
        return ServletContextInitializer { ctx ->
            val cookieConfig = ctx.sessionCookieConfig
            cookieConfig.name = "SCRUMCELL"
            cookieConfig.isHttpOnly = true
            cookieConfig.isSecure = true
            cookieConfig.path = "/"
            cookieConfig.maxAge = sessionAge
        }
    }
}