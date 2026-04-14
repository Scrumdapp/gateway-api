package com.scrumdapp.gateway.configuration

import com.scrumdapp.gateway.handlers.exceptions.CustomAccessDeniedHandler
import com.scrumdapp.gateway.handlers.exceptions.CustomAuthenticationEntrypoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

@Configuration
class SecurityConfig(
    private val customAuthenticationEntryPoint: CustomAuthenticationEntrypoint,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler,
    private val authenticationSuccessHandler: AuthenticationSuccessHandler,
    private val authenticationFailureHandler: AuthenticationFailureHandler
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/login/**", "/logoutsuccessful", "/oauth2/**", "/.well-known/jwks.json").permitAll()
                it.requestMatchers("/error").permitAll()
                it.anyRequest().authenticated()
            }
            .oauth2Login { oauth2 -> oauth2
                .loginPage("/oauth2/authorization/discord")
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
            }
            .exceptionHandling { h ->
                h.authenticationEntryPoint(customAuthenticationEntryPoint)
                h.accessDeniedHandler(customAccessDeniedHandler)
            }
            .logout {logout -> logout.logoutSuccessUrl("/logoutsuccessful")}

        return http.build()
    }
}