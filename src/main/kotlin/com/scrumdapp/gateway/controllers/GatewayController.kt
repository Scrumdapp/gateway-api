package com.scrumdapp.gateway.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GatewayController {

    @GetMapping
    fun login(): String = "redirect:/oauth2/authorization/discord"
}