package com.scrumdapp.gateway.filters

import com.scrumdapp.gateway.services.JwtService
import org.springframework.http.HttpHeaders
import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.Order
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import org.springframework.web.servlet.function.HandlerFilterFunction
import org.springframework.web.servlet.function.ServerResponse
import reactor.core.publisher.Mono
import java.time.Instant


class JwtFilter() {

    @Bean
    fun jwtAdditionFilter(): HandlerFilterFunction<ServerResponse, ServerResponse> {
        return HandlerFilterFunction { request, next ->

            next.handle(request)
        }
    }
}

data class InternalJwt(
    val token: String,
    val expiresAt: Instant
) {
    fun isExpired(): Boolean {
        return expiresAt.minusSeconds(2) < Instant.now()
    }
}

@Component
class JwtWebFilter(
    private val jwtService: JwtService,
): WebFilter {

    companion object {

    }

    override fun filter(
        exchange: ServerWebExchange,
        chain: WebFilterChain
    ): Mono<Void> {
        val session = exchange.session

        return exchange.getPrincipal<Authentication>()
            .flatMap { auth ->
                session.flatMap { session ->
                    var cachedJwt = session.getAttribute("JWT_SESSION_KEY") as? InternalJwt

                    if (cachedJwt == null || cachedJwt.isExpired()) {
                        cachedJwt = generateJwtToken(auth)
                        session.attributes["JWT_SESSION_KEY"] = cachedJwt
                    }

                    val mutatedReq = exchange.request
                        .mutate()
                        .headers {
                            it.remove(HttpHeaders.COOKIE)
                            it.setBearerAuth(cachedJwt.token)
                        }
                        .build()

                    chain.filter(exchange.mutate().request(mutatedReq).build())
                }
            }
    }

    private fun generateJwtToken(auth: Authentication): InternalJwt {
        val expiresAt = Instant.now().plusSeconds(300)

        val token = jwtService.generateJwtToken(
            subject = auth.name,
            claims = mapOf("user_id" to 24, "user_groups" to 1) //TODO( Change this to actual information from group & userService )
        )

        return InternalJwt(token, expiresAt)
    }

}