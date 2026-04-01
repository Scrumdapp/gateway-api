package com.scrumdapp.gateway.handlers.exceptions

import com.scrumdapp.gateway.utils.ExceptionUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ControllerExceptionHandler(
    private val exceptionUtils: ExceptionUtils
) {

    @ExceptionHandler(ApplicationException::class)
    fun handleApplicationException(e: ApplicationException): ResponseEntity<ApiResponse> {
        exceptionUtils.logException(e)
        return ResponseEntity.status(e.code).body(ApiResponse(e.code, e.message))
    }
}
