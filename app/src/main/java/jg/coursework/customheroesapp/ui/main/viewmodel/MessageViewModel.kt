package jg.coursework.customheroesapp.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jg.coursework.customheroesapp.data.api.ApiHelper
import jg.coursework.customheroesapp.data.model.AuthResponse
import jg.coursework.customheroesapp.data.model.Message
import jg.coursework.customheroesapp.util.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessageViewModel(private val apiHelper: ApiHelper) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    fun getChats() {
        viewModelScope.launch {
            try {
                val newMessages = apiHelper.getChats()
                _messages.tryEmit(newMessages)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun getMessages(chatId: Long) {
        viewModelScope.launch {
            try {
                val newMessages = apiHelper.getMessages(chatId)
                _messages.tryEmit(newMessages)
            } catch (e: Exception) {

            }
        }
    }

    fun uploadMessages(message: Message) {
        viewModelScope.launch {
            try {
                apiHelper.uploadMessage(message)
                val newMessages = apiHelper.getMessages(message.chatRoomId)
                _messages.tryEmit(newMessages)
            } catch (e: Exception) {

            }
        }
    }
}
