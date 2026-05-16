package com.scrumdapp.gateway.security.auth

import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CookieConfig {

    @Bean
    fun servletCookieInitializer(): ServletContextInitializer {
        return ServletContextInitializer { ctx ->
            val cookieConfig = ctx.sessionCookieConfig
            cookieConfig.name = "SCRUMCELL"
            cookieConfig.isHttpOnly = true
            cookieConfig.isSecure = true
            cookieConfig.path = "/"
            cookieConfig.maxAge = 3600 * 24 * 7 // 1 week
        }
    }
}