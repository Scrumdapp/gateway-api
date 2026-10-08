package com.scrumdapp.gateway.passports

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.SessionAttribute

@RestController
@RequestMapping("/api/passports")
class PassportController(
    val passportService: PassportService
) {

    @DeleteMapping("/@me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun invalidOwnPassport(
        @SessionAttribute userId: Long,
    ) {
        passportService.invalidatePassport(userId)
    }
}