package com.scrumdapp.gateway.security.auth

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class GatewayAuthentication(
    val principal: AuthPrincipal,
    authorities: Collection<GrantedAuthority>,
): AbstractAuthenticationToken(authorities) {

    init {
        isAuthenticated = true
    }

    override fun getCredentials(): Any?= null

    override fun getPrincipal(): Any = principal
}