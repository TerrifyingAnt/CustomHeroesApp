package jg.coursework.customheroesapp.data.model

data class UserRegisterRequest(
    val login: String,
    val password: String,
    val type: String,
    val phoneNumber: String,
    val avatarSource: String
)
