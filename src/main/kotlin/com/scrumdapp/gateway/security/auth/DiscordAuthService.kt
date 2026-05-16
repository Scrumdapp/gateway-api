package com.scrumdapp.gateway.security.auth

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.toEntity
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue

@JsonIgnoreProperties(ignoreUnknown = true)
data class DiscordUser(
    val id: String,
    val username: String,
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


@Component
class DiscordService(
    builder: RestClient.Builder,
) {

    private val restClient = builder
        .baseUrl("https://discord.com/api/v10")
        .build()

    fun getUser(token: String): Result<DiscordUser> {
        return discordRequest("/users/@me", token)
    }

    fun getGuilds(token: String): Result<List<DiscordGuild>> {
        return discordRequest("/users/@me/guilds", token)
    }

    fun getGuildMember(token: String, guildId: String): Result<DiscordGuildMember> {
        return discordRequest("/users/@me/guilds/${guildId}/member", token)
    }

    fun isInServer(accessToken: String, authGuild: String): Boolean {
        val guilds = getGuilds(accessToken)
        if (guilds.isFailure) throw RuntimeException("There was an error while fetching guilds.")

        for (guild in guilds.getOrThrow()) {
            if (guild.id == authGuild) {
                return true
            }
        }
        return false
    }

    private inline fun <reified T> discordRequest(endpoint: String, token: String): Result<T> {
        return try {
            val response = restClient.get()
                .uri(endpoint)
                .header(HttpHeaders.AUTHORIZATION, String.format("Bearer %s", token))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity<String>()

            if (response.statusCode == HttpStatus.OK) {
                val body = response.body?: return Result.failure(IllegalStateException("Body is missing"))
                val mapper = jacksonObjectMapper()
                val parsed = mapper.readValue<T>(body)

                Result.success(parsed)
            } else {
                Result.failure(Exception("Request failed ${response.statusCode}, body: ${response.body}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}