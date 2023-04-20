package jg.coursework.customheroesapp.data.api

import jg.coursework.customheroesapp.data.model.Message
import jg.coursework.customheroesapp.data.model.UserLoginRequest
import jg.coursework.customheroesapp.data.model.UserRegisterRequest
import jg.coursework.customheroesapp.util.PreferenceManager
import jg.coursework.customheroesapp.util.PreferenceManager.Companion.ACCESS_TOKEN


class ApiHelper(private val apiService: ApiService, private val preferenceManager: PreferenceManager) {
    suspend fun auth(request: UserLoginRequest) = apiService.auth(request)
    suspend fun register(request: UserRegisterRequest) = apiService.register(request)
    suspend fun getChats() = apiService.getChats("Bearer " + preferenceManager.getAccessToken())
    suspend fun getUser() = apiService.getUser("Bearer " + preferenceManager.getAccessToken())

    suspend fun getMessages(chatRoomId: Long) = apiService.getMessages("Bearer " + preferenceManager.getAccessToken(), chatRoomId)

    suspend fun uploadMessage(message: Message) = apiService.uploadMessage("Bearer " + preferenceManager.getAccessToken(), message)
}