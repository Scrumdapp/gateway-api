package com.scrumdapp.gateway.services

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.scrumdapp.gateway.ServiceProperties
import com.scrumdapp.gateway.discordUser.UpsertUser
import com.scrumdapp.gateway.passports.PassportContent
import com.scrumdapp.gateway.passports.PassportToken
import com.scrumdapp.gateway.security.jwt.JwtService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient.builder
import org.springframework.web.client.toEntity
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.time.Instant

@JsonIgnoreProperties(ignoreUnknown = true)
data class ScrumdappUser(
    val id: Long,
    val role: String,
)

@Service
class DownstreamRequestService(
    private val jwtService: JwtService,
    private val serviceProperties: ServiceProperties,
    @Value($$"${JWT_LIFETIME}") private val passportLifeTime: Long = 60*5
) {

    private val mapper = jacksonObjectMapper()

    private var jwtToken = genGatewayToken()

    fun getPassport(userId: Long): PassportContent {
        return performRequest(HttpMethod.GET, serviceProperties.getUrl("users"), "/users/$userId/passport", PassportContent::class.java)
    }

    fun upsertUser(user: UpsertUser): ScrumdappUser {
        return performRequest(HttpMethod.PATCH, serviceProperties.getUrl("users"), "/users/gateway", ScrumdappUser::class.java, user)
    }

    private fun <T> performRequest(
        method: HttpMethod,
        baseUrl: String,
        uri: String,
        classZ: Class<T>,
        body: Any? = null,
        ): T {

        if (jwtToken.isExpired()) jwtToken = genGatewayToken()

        val reqBuilder = builder().baseUrl(baseUrl).build()

        try {
            val reqSpec = reqBuilder.method(method)
                .uri(uri)
                .header("Authorization", "Bearer ${jwtToken.token}")
                .accept(MediaType.APPLICATION_JSON)

            val resSpec = if (body != null && method in listOf(HttpMethod.PATCH, HttpMethod.POST)) {
                reqSpec.body(body)
            } else {
                reqSpec
            }

            val res = resSpec.retrieve().toEntity<String>()

            if (res.statusCode == HttpStatus.OK) {
            val body = res.body ?:  throw IllegalStateException("Request body is null")
            return mapper.readValue(body, classZ)

        } else {
            throw Exception("Request failed with status ${res.statusCode}, body: ${res.body}")
        }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }

    private fun genGatewayToken(): PassportToken {

        val expiresAt = Instant.now().plusSeconds(passportLifeTime)
        val content = PassportContent(
            userId = -1,
            roles = listOf("GATEWAY")
        )

        val token = jwtService.generateJwtToken(
            subject = "-1",
            claims = content.toJwtClaim()
        )

        return PassportToken(token, expiresAt)
    }

}