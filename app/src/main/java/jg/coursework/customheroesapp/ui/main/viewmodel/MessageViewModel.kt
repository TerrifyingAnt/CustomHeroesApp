package jg.coursework.customheroesapp.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jg.coursework.customheroesapp.data.api.ApiHelper
import jg.coursework.customheroesapp.data.model.AuthResponse
import jg.coursework.customheroesapp.data.model.Chat
import jg.coursework.customheroesapp.data.model.Message
import jg.coursework.customheroesapp.data.model.User
import jg.coursework.customheroesapp.util.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Thread.State

class MessageViewModel(private val apiHelper: ApiHelper) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    private val tempChat: Chat = Chat(0, "")
    private val _chat = MutableStateFlow<Chat>(tempChat)
    val chat: StateFlow<Chat> = _chat

    private val _chatNotExist = MutableStateFlow<List<String>>(emptyList())
    val chatNotExist: StateFlow<List<String>> = _chatNotExist

    private val _state = MutableStateFlow<ChatState>(ChatState.Loading)
    val state: StateFlow<ChatState> = _state

    sealed class ChatState {
        object Loading : ChatState()
        data class Success(val chat: Chat) : ChatState()
        data class Error(val message: String) : ChatState()
    }
    fun getChats() {
        viewModelScope.launch {
            try {
                val newChats = apiHelper.getChats()
                print("in viewModel: xd" + newChats)
                _chats.tryEmit(newChats)
            } catch (e: Exception) {
                print("Произошла ошибка в получении чатов")
            }
        }
    }

    fun getMessages(chatId: Long) {
        viewModelScope.launch {
            try {
                val newMessages = apiHelper.getMessages(chatId)
                _messages.tryEmit(newMessages)
            } catch (e: Exception) {
                print("Произошла ошибка в получении сообщений")
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
                print("Произошла ошибка в отправке и получении собщения")
            }
        }
    }

    fun getChatNotExist() {
        viewModelScope.launch {
            try {
                val newChatsWith = apiHelper.getChatNotExist()
                _chatNotExist.tryEmit(newChatsWith)
            } catch (e: Exception) {
                print("Произошла ошибка в получении списка пользователей для создания нового чата")
            }
        }
    }

    fun createChat(login: String) {
        viewModelScope.launch {
            _state.value = ChatState.Loading
            try {
                val newChats = apiHelper.createChat(login)
                print("in viewModel: xd" + newChats)
                _chat.tryEmit(newChats)
                _state.value = ChatState.Success(newChats)

            }
            catch (e: Exception) {
                print("Произошла ошибка в создания нового чата")
                print(e)
            }
        }
    }
}
