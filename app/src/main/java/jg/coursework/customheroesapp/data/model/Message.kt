package jg.coursework.customheroesapp.data.model

data class Message(
    val username: Long,
    val fromUser: Long,
    val toUser: Long,
    val date: String,
    var content: String,
    val chatRoomId: Long
)
