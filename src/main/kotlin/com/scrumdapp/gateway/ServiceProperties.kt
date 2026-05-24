package com.scrumdapp.gateway

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "gateway.services")
class ServiceProperties {
    lateinit var urls: Map<String, String>

    fun getUrl(service: String): String {
        return urls[service.lowercase()] ?: error("Service was not configured")
    }
}