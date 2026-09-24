package com.scrumdapp.gateway.exceptions

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ControllerExceptionHandler(
    private val exceptionService: ExceptionService
) {

    @ExceptionHandler(Exception::class)
    fun handle(ex: Exception, request: HttpServletRequest): ResponseEntity<ApiResponse> {
        exceptionService.logException(ex, request)
        val body = exceptionService.mapException(ex)
        return ResponseEntity.status(body.code).body(body)
    }
}