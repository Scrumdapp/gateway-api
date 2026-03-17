package com.scrumdapp.gateway.configuration

import org.springdoc.webmvc.core.fn.SpringdocRouteBuilder.route
import org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.function.RouterFunction
import org.springframework.web.servlet.function.ServerResponse


@Configuration
class TestConfiguration {
}