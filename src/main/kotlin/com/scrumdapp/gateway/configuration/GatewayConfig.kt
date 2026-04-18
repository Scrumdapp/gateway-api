package com.scrumdapp.gateway.configuration

import com.scrumdapp.gateway.gatewayfilters.PassportFilters
import com.scrumdapp.gateway.gatewayfilters.PostUpstreamFilter
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath
import org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse


@Configuration
class GatewayConfig {

    @Bean
    fun gatewayRoutes(
        passportFilters: PassportFilters
    ): RouterFunction<ServerResponse> {

        val postUpstreamFilter = PostUpstreamFilter(
            mapOf(
                "/api/invites/{id}/accept" to listOf(HttpMethod.POST),
                "/api/users/{id}" to listOf(HttpMethod.POST, HttpMethod.PATCH)
            ),
        )

        val routes: RouterFunction<ServerResponse> =
            route()
                .filter(passportFilters.insertPassport())

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
                    .filter(postUpstreamFilter.invalidatePassport())
                    .build()
                )
                .add(route("invites")
                    .route(path("/invites/**"), http())
                    .filter(lb("INVITE-SERVICE"))
                    .filter(postUpstreamFilter.invalidatePassport())
                    .build()
                )

                .before(rewritePath("/api/(?<segment>.*)", $$"/${segment}"))
            .build()

       return routes
    }
}