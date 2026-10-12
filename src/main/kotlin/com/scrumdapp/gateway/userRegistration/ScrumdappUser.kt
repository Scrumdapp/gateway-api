package com.scrumdapp.gateway.userRegistration

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ScrumdappUser(
    val id: Long,
    val first_name: String,
    val last_name: String,
    val roles: List<String>,
    val avatar: String? = null,
)