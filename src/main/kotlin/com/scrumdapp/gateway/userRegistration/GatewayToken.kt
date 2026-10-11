package com.scrumdapp.gateway.userRegistration

import java.time.Instant

data class GatewayToken(
    val token: String,
    val expiresAt: Instant
) {
    fun isExpired(): Boolean {
        return Instant.now().isAfter(expiresAt)
    }
}