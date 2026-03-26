package com.scrumdapp.gateway.configuration

import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.HandlerFilterFunction
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse


@Configuration
class GatewayConfig {

    @Bean
    fun sessionCookieFilter(): HandlerFilterFunction<ServerResponse, ServerResponse> {
        return HandlerFilterFunction { request, next ->

            val sessionToken = request.cookies().getFirst("SessionToken")?.value

//            if (sessionToken.isNullOrBlank()) {
//                return@HandlerFilterFunction ServerResponse.status(HttpStatus.UNAUTHORIZED).build()
//            }

            // Do something with databases or user service here. Maybe it is cleaner if we handle session tokens here and add information from userservice.

            next.handle(request)
        }
    }

    @Bean
    fun gatewayRoutes(sessionCookieFilter: HandlerFilterFunction<ServerResponse, ServerResponse>): RouterFunction<ServerResponse> {

        val routes: RouterFunction<ServerResponse> =
            route()
                .add(route("groups")
                    .GET("/api/groups/**", http())
                    .before(uri("http://127.0.0.1:3000"))
                    .build()
                )
                .add(route("users")
                    .GET("/users/**", http())
                    .build()
                )

                .before(rewritePath("/api/(?<segment>.*)", $$"/${segment}"))
            .build()


        return routes.filter(sessionCookieFilter)
    }
}