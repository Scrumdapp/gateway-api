package com.scrumdapp.gateway.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity, successHandler: AuthenticationSuccessHandler): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/login/**", "/oauth2/**").permitAll()
                it.anyRequest().authenticated()
            }
            .oauth2Login { oauth2 -> oauth2
                .loginPage("/oauth2/authorization/discord")
                .successHandler(successHandler
            )}
            .logout {logout -> logout.logoutSuccessUrl("/")}

        return http.build()
    }
}