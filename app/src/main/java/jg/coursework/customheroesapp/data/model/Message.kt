package jg.coursework.customheroesapp.data.model

data class Message(
    val fromUser: String,
    var toUser: String,
    val date: String,
    var content: String,
    val chatRoomId: Long
)
