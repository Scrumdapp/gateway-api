package com.scrumdapp.gateway.configuration

import com.scrumdapp.gateway.gatewayfilters.JwtFilters
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path
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

                .add(route("checkpoints")
                    .route(path("/api/groups/{groupId}/sessions/**"), http())
                    .filter(lb("CHECKPOINT-SERVICE"))
                    .build()
                )
                .add(route("groups")
                    .route(path("/api/groups/**"), http())
                    .filter(lb("GROUPS-SERVICE"))
                    .build()
                )
                .add(route("users")
                    .route(path("/users/**"), http())
                    .filter(lb("USER-SERVICE"))
                    .build()
                )
                .add(route("invites")
                    .route(path("/invites/**"), http())
                    .filter(lb("INVITE-SERVICE"))
                    .build()
                )

                .before(rewritePath("/api/(?<segment>.*)", $$"/${segment}"))
            .build()

       return routes
    }
}