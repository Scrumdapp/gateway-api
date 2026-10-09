package com.scrumdapp.gateway.passports

data class PassportContent(
    val userId: Int,
    val userGroups: List<Int> = listOf(),
    val roles: List<String>
) {
    fun toJwtClaim(): Map<String, Any> {
        return mapOf("userId" to userId, "userGroups" to userGroups, "roles" to roles)
    }
}