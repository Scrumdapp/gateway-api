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

    fun handleLogin(email: String, groups: ArrayList<String>): Long {
        val body = UpsertUser(
            email = email,
            name = getName(email),
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

    private fun getName(email: String): String {
        var name = email.trim().split("@").first()
        name.replace(".", " ")

        val re = Regex("[^A-Za-z ]")
        name = re.replace(name, "")

        return name
    }
}