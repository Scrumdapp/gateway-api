package com.scrumdapp.gateway.security.auth

import java.io.Serializable

data class AuthPrincipal(
    val userId: Long,
    val roles: Set<String>
): Serializable