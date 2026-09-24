package com.scrumdapp.gateway.passports

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.TimeToLive
import java.util.concurrent.TimeUnit

@RedisHash("scrumdapp:passports")
class Passport(
    @Id
    val userId: Long,

    @TimeToLive(unit = TimeUnit.SECONDS)
    val timeToLive: Long,

    val token: String
)

data class PassportContent(
    val userId: Int,
    val userGroups: List<Int> = listOf(),
    val roles: List<String>
) {
    fun toJwtClaim(): Map<String, Any> {
        return mapOf("userId" to userId, "userGroups" to userGroups, "roles" to roles)
    }
}