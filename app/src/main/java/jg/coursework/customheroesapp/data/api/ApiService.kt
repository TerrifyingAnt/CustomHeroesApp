package jg.coursework.customheroesapp.data.api

import jg.coursework.customheroesapp.data.model.AuthResponse
import jg.coursework.customheroesapp.data.model.Message
import jg.coursework.customheroesapp.data.model.UserLoginRequest
import jg.coursework.customheroesapp.data.model.UserRegisterRequest

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface ApiService {
    @POST("api/auth/login")
    suspend fun auth(@Body request: UserLoginRequest): AuthResponse

    @POST("api/auth/register")
    suspend fun register(@Body request: UserRegisterRequest) : AuthResponse

    @GET("test/chats")
    suspend fun getChats(@Header("Authorization") token: String): List<Message>

    @POST("test/messages")
    suspend fun getMessages(@Header("Authorization") token: String, @Body chatRoomId: Long): List<Message>

    @GET("/me")
    suspend fun getUser(@Header("Authorization") token: String): UserRegisterRequest

    @POST("/test/upload")
    suspend fun uploadMessage(@Header("Authorization") token: String, @Body message: Message)
}

