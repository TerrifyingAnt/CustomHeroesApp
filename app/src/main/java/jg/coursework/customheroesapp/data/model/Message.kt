package jg.coursework.customheroesapp.data.model

import java.util.Date

data class Message(
    val fromUser: Long,
    val toUser: Long,
    val id: Date,
    val content: String
)
