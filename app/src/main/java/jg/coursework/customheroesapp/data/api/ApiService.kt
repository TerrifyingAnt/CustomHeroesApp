package jg.coursework.customheroesapp.data.api

import jg.coursework.customheroesapp.data.model.*

import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    @POST("api/auth/login")
    suspend fun auth(@Body request: UserLoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: UserRegisterRequest) : AuthResponse

    @GET("test/chats")
    suspend fun getChats(@Header("Authorization") token: String): List<Chat>

    @POST("test/messages")
    suspend fun getMessages(@Header("Authorization") token: String, @Body chatRoomId: Long): List<Message>

    @GET("test/me")
    suspend fun getMe(@Header("Authorization") token: String): User

    @POST("test/user")
    suspend fun getUser(@Header("Authorization") token: String, @Body id: Int): User

    @POST("/test/upload")
    suspend fun uploadMessage(@Header("Authorization") token: String, @Body message: Message)

    @GET("test/new-chat-with")
    suspend fun getChatNotExist(@Header("Authorization") token: String): List<String>

    @POST("test/new-chat")
    suspend fun createChat(@Header("Authorization") token: String, @Body login: String): Chat
}

