package com.scrumdapp.gateway.controllers

import com.scrumdapp.gateway.handlers.exceptions.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
class GatewayController {

    @GetMapping("/login")
    fun loginRedirect(): RedirectView {
        return RedirectView("oauth2/authorization/discord")
    }

    @GetMapping("/logoutsuccessful")
    fun logoutRedirect(): ResponseEntity<ApiResponse> {
        val body = ApiResponse(
            code = HttpStatus.OK.value(),
            message = "Logout successful"
        )
        return ResponseEntity.status(HttpStatus.OK.value()).body(body)
    }
}