package com.scrumdapp.gateway.discordUser

import com.scrumdapp.gateway.exceptions.NoAccessException
import com.scrumdapp.gateway.exceptions.ServerFaultException
import com.scrumdapp.gateway.services.DownstreamRequestService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

data class UpsertUser(
    val discordId: Long,
    val name: String,
    val role: String,
    val avatar: String? = null,
)

@Component
class DiscordUserService(
    private val discordService: DiscordService,
    private val requestService: DownstreamRequestService,
    @Value($$"${DISCORD_ALLOWED_GUILD}") private val allowedGuild: String
) {

    fun handleLogin(accessToken: String): Long {

        if (!discordService.isInServer(accessToken, allowedGuild)) {
            throw NoAccessException(message = "Your Discord account is not authorized to access this resource")
        }

        val discordUser = discordService.getUser(accessToken).getOrElse {
            throw ServerFaultException(message = "Could not fetch user from Discord")
        }

        val guildUser = discordService.getGuildMember(accessToken, allowedGuild).getOrElse {
            throw ServerFaultException(message = "Could not fetch user from Discord")
        }

        val body = UpsertUser(
            discordUser.id.toLong(),
            getName(guildUser, discordUser),
            getRole(guildUser),
            getAvatar(guildUser, discordUser),
        )

        val user = requestService.upsertUser(body)
        return user.id
    }

    private fun getName(discordGuildMember: DiscordGuildMember, discordUser: DiscordUser): String {
        val name = discordGuildMember.nick ?: discordUser.global_name ?: discordUser.username
        return name.trim()
    }

    private fun getRole(discordGuildMember: DiscordGuildMember): String {
        return if (discordGuildMember.roles.isEmpty()) {
            "student"
        } else if (discordGuildMember.roles.find { it == 730402010351796314} != null) {
            "coach"
        } else {
            "student"
        }
    }

    private fun getAvatar(discordGuildMember: DiscordGuildMember, discordUser: DiscordUser): String? {

        return if (discordGuildMember.avatar != null) {
            "https://cdn.discordapp.com/guilds/$allowedGuild/users/${discordUser.id}/avatars/${discordGuildMember.avatar}.png"
        } else if (discordUser.avatar != null) {
            "https://cdn.discordapp.com/avatars/${discordUser.id}/${discordUser.avatar}.png"
        } else {
            null
        }
    }
}