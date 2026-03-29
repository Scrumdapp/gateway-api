package com.scrumdapp.gateway.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
class GatewayController {

    @GetMapping("/login")
    fun loginRedirect(): RedirectView {
        return RedirectView("oauth2/authorization/discord")
    }
}