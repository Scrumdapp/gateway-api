package com.scrumdapp.gateway.configuration

import com.scrumdapp.gateway.ServiceProperties
import com.scrumdapp.gateway.passports.PassportFilters
import com.scrumdapp.gateway.passports.PassportInvalidationFilter
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri
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
        passportFilters: PassportFilters,
        services: ServiceProperties
    ): RouterFunction<ServerResponse> {

        // Endpoints registered here will cause any existing passport to be invalidated
        val passportInvalidationFilter = PassportInvalidationFilter(
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
                    .route(path("/api/groups/{groupId}/checkpoints/**"), http())
                    .before(uri(services.getUrl("checkpoints")))
                    .build()
                )
                .add(route("groups")
                    .route(path("/api/groups/**"), http())
                    .before(uri(services.getUrl("groups")))
                    .build()
                )
                .add(route("users")
                    .route(path("api/users/**"), http())
                    .filter(passportInvalidationFilter.invalidatePassport())
                    .before(uri(services.getUrl("users")))
                    .build()
                )
                .add(route("invites")
                    .route(path("api/invites/**"), http())
                    .filter(passportInvalidationFilter.invalidatePassport())
                    .before(uri(services.getUrl("invites")))
                    .build()
                )
                .add(route("trends")
                    .route(path("/api/trends/**"), http())
                    .before(uri(services.getUrl("trends")))
                    .build()
                )

            .before(rewritePath("/api/(?<segment>.*)", $$"/${segment}"))
            .build()

       return routes
    }
}