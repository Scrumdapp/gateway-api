package com.scrumdapp.gateway.services

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.client.RestClient

@JsonIgnoreProperties(ignoreUnknown = true)
data class DiscordUser(
    val id: String,
    val userame: String,
    val avatar: String?,
    val global_name: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DiscordGuild(
    val id: String,
    val name: String,
    val icon: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DiscordGuildMember(
    val user: DiscordUser?,
    val nickname: String?,
    val avatar: String?,
)

interface DiscordService {
    fun getUser(token: String): Result<DiscordUser>
    fun getGuild(token: String): Result<DiscordGuild>
    fun getGuildMember(token: String): Result<DiscordGuildMember>
}


class DiscordServiceImpl(
    private final var restClient: RestClient
) : DiscordService {

    public fun DiscordServiceImpl() {
        this.restClient = RestClient.builder()
            .baseUrl("https://discord.com/api")
            .build()
    }

    override fun getUser(token: String): Result<DiscordUser> {
        TODO("Not yet implemented")
    }

    override fun getGuild(token: String): Result<DiscordGuild> {
        TODO("Not yet implemented")
    }

    override fun getGuildMember(token: String): Result<DiscordGuildMember> {
        TODO("Not yet implemented")
    }

    private suspend inline fun <reified T> discordRequest(endpoint: String, token: String): Result<T> {
        val response = restClient.get()
            .uri(endpoint)
            .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
    }
}