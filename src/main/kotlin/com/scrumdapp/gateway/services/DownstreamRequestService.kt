package com.scrumdapp.gateway.services

import com.scrumdapp.gateway.passports.PassportContent
import com.scrumdapp.gateway.passports.PassportService
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient.builder
import org.springframework.web.client.toEntity
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue

@Service
class DownstreamRequestService(
    private val passportService: PassportService
) {

    private val mapper = jacksonObjectMapper()

    private var jwtToken = passportService.generateGatewayPassport()

    fun getPassport(userId: Int): PassportContent {
        return performRequest("${userId}", "/users/$userId/passport")
    }

    private inline fun <reified T> performRequest(baseUrl: String, uri: String): T{

        if (jwtToken.isExpired()) jwtToken = passportService.generateGatewayPassport()

        val reqBuilder = builder().baseUrl(baseUrl).build()

        try {
            val res = reqBuilder.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $jwtToken")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity<String>()
            if (res.statusCode == HttpStatus.OK) {
            val body = res.body ?:  throw IllegalStateException("Request body is null")
            val parsedBody = mapper.readValue<T>(body)

            return parsedBody
        } else {
            throw Exception("Request failed with status ${res.statusCode}, body: ${res.body}")
        }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }
}