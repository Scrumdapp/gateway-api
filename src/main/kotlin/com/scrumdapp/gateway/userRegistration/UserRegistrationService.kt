package com.scrumdapp.gateway.userRegistration

import com.scrumdapp.gateway.exceptions.NoAccessException
import org.springframework.stereotype.Service

data class UpsertUser(
    val email: String,
    val name: String,
    val role: String,
    val avatar: String? = null,
)

@Service
class UserRegistrationService(
    private val requestService: DownstreamRequestService
) {

    fun handleLogin(email: String, groups: ArrayList<String>, name: String): Long {
        val body = UpsertUser(
            email = email,
            name = name,
            role = getRole(groups),
            avatar = null,
        )

        val user = requestService.upsertUser(body)
        return user.id
    }

    private fun getRole(groups: ArrayList<String>): String {
        return if (groups.contains("hu_students")) {
            "student"
        } else if (groups.contains("hu_teachers")) {
            "coach"
        } else {
            throw NoAccessException(message="No valid role provided")
        }
    }
}