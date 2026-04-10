package com.example.login.data.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO para la respuesta de GET /users/:id
 * Los campos coinciden exactamente con lo que devuelve el backend Express/Sequelize.
 */
data class UserDto(
    @SerializedName("id")           val id: Int,
    @SerializedName("username")     val username: String,
    @SerializedName("name")         val name: String,
    @SerializedName("bio")          val bio: String?,
    @SerializedName("profileImage") val profileImage: String?,
    @SerializedName("email")        val email: String
)
