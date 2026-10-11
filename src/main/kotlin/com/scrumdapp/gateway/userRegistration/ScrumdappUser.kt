package com.scrumdapp.gateway.userRegistration

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ScrumdappUser(
    val id: Long
)