package com.scrumdapp.gateway.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf {csrfConfigurer -> csrfConfigurer.disable()}
            .authorizeHttpRequests { auth -> auth
                .requestMatchers("/login/**", "/oath2/**").permitAll()
                .anyRequest().authenticated()
            }
            .oauth2Login(withDefaults()) // To Do change this
            .logout {logout -> logout.logoutSuccessUrl("/")}

        return http.build()
    }
}