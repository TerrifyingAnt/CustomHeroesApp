package jg.coursework.customheroesapp.data.model

import com.google.gson.annotations.SerializedName


data class Message(
    val fromUser: String,
    var toUser: String,
    val date: String,
    var content: String,
    val chatRoomId: Long
)
