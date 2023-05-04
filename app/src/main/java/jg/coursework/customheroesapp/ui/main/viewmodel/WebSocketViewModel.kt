package jg.coursework.customheroesapp.ui.main.viewmodel

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.*
import jg.coursework.customheroesapp.util.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.Request
import okio.ByteString
import java.util.concurrent.TimeUnit

class WebSocketViewModel : ViewModel() {
    private var webSocket: WebSocket? = null
    private val _messages = MutableLiveData<String>()
    val messages: LiveData<String>
        get() = _messages

    init {
        connectWebSocket()
    }

    private fun connectWebSocket() {
        val request = Request.Builder()
            .url("ws://10.0.2.2:8081/topic/publicChatRoom")
            .addHeader("Authorization", "Bearer " + "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ0ZXN0MkB0ZXN0LnRlc3QiLCJyb2xlcyI6IkNVU1RPTUVSIiwiaWF0IjoxNjgzMjE1MjQ5LCJleHAiOjE2ODMyMTg4NDl9.SNts4ElvrJ2LAIGpywHNbDoDkB4FTc3VByS9PKO6CcJP3gwfAFNB2hDAIyMlEqm2")
            .build()
        val client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()


        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                println("WebSocket connected!")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                viewModelScope.launch(Dispatchers.IO) {
                    _messages.postValue(text)
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                println("WebSocket connection closed with code $code, reason: $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                println("WebSocket connection failed: ${t.message}")
            }
        })
    }

    override fun onCleared() {
        super.onCleared()
        webSocket?.close(1000, null)
    }

}