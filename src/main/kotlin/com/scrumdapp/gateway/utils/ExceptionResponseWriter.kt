package com.scrumdapp.gateway.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.scrumdapp.gateway.handlers.exceptions.ApiResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Component

@Component
class ExceptionResponseWriter(
    private val objectMapper: ObjectMapper
) {
    fun write(res: HttpServletResponse, body: ApiResponse) {
        res.status = body.code
        res.contentType = MediaType.APPLICATION_JSON_VALUE

        objectMapper.writeValue(res.outputStream, body)
    }
}