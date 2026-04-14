package com.scrumdapp.gateway.configuration

import com.scrumdapp.gateway.gatewayfilters.JwtFilters
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse


@Configuration
class GatewayConfig {

    @Bean
    fun gatewayRoutes(
        jwtFilters: JwtFilters
    ): RouterFunction<ServerResponse> {

        val routes: RouterFunction<ServerResponse> =
            route()
                .filter(jwtFilters.filterJwtSession())
                .add(route("groups")
                    .GET("/api/groups/**", http())
                    .filter(lb("CHECKPOINT-SERVICE"))

                    .build()
                )
                .add(route("users")
                    .GET("/users/**", http())
                    .build()
                )

                .before(rewritePath("/api/(?<segment>.*)", $$"/${segment}"))
            .build()

       return routes
    }
}