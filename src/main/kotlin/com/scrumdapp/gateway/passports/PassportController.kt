package com.scrumdapp.gateway.passports

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.SessionAttribute

@RestController
@RequestMapping("/api/passports")
class PassportController(
    val passportService: PassportService
) {

    @GetMapping("/@me/invalidate")
    fun invalidOwnPassport(
        @SessionAttribute userId: Long,
    ): ResponseEntity<Void> {
        passportService.invalidatePassport(userId)
        return ResponseEntity<Void>(HttpStatus.NO_CONTENT)
    }
}