package com.scrumdapp.gateway.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity, successHandler: AuthenticationSuccessHandler): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth -> auth
                .requestMatchers("/login/**", "/oath2/**").permitAll()
                .anyRequest().authenticated()
            }
            .oauth2Login { oauth2 -> oauth2.successHandler(successHandler)}
            .logout {logout -> logout.logoutSuccessUrl("/")}

        return http.build()
    }
}