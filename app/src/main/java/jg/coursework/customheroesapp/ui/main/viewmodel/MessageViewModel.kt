package jg.coursework.customheroesapp.ui.main.viewmodel

import androidx.datastore.preferences.preferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rabbitmq.client.CancelCallback
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.DeliverCallback
import com.rabbitmq.client.Delivery
import jg.coursework.customheroesapp.data.api.ApiHelper
import jg.coursework.customheroesapp.data.model.Chat
import jg.coursework.customheroesapp.data.model.Message
import jg.coursework.customheroesapp.util.PreferenceManager
import jg.coursework.customheroesapp.util.Status
import jg.coursework.customheroesapp.util.Status.LOADING
import jg.coursework.customheroesapp.util.Status.SUCCESS
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import kotlin.math.log

class MessageViewModel(private val apiHelper: ApiHelper, private val preferenceManager: PreferenceManager) : ViewModel() {
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

    private val _login = MutableStateFlow<String>("")
    val login: StateFlow<String> = _login

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Loading)
    val loginState: StateFlow<LoginState> = _loginState

    sealed class ChatState {
        object Loading : ChatState()
        data class Success(val chat: Chat) : ChatState()
        data class Error(val message: String) : ChatState()
    }

    sealed class LoginState {
        object Loading : LoginState()
        data class Success(val login: String) : LoginState()
        data class Error(val message: String) : LoginState()
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

    fun main(login: String) {
        val factory = ConnectionFactory()
        factory.setHost("10.0.2.2")
        factory.setPort(5672)
        factory.setUsername("guest")
        factory.setPassword("guest")
        val connection = factory.newConnection() /*newConnection("amqp://guest:guest@10.0.2.2:5672/")*/
        val channel = connection.createChannel()
        val consumerTag = "SimpleConsumer"

        channel.queueDeclare("test_queue", false, false, false, null)

        println("[$consumerTag] Waiting for messages...")
        val deliverCallback = DeliverCallback { consumerTag: String?, delivery: Delivery ->
            val message = String(delivery.body, StandardCharsets.UTF_8)
            println("[$consumerTag] Received message: '$message'")
        }
        val cancelCallback = CancelCallback { consumerTag: String? ->
            println("[$consumerTag] was canceled")
        }

        channel.basicConsume(login, true, consumerTag, deliverCallback, cancelCallback)
    }

    fun getLogin() {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val tempLogin = preferenceManager.getUser()?.login
                print("in viewModel: xd" + tempLogin)
                if (tempLogin != null && tempLogin != "") {
                    _login.tryEmit(tempLogin)
                    _loginState.value = LoginState.Success(tempLogin)
                }
            }catch (e: Exception) {
                print("Произошла ошибка в ПОЛУЧЕНИИ БЛЯДСКОГО ЛОГИНА ЕБАНЫЙ ДАТА СТОР")
                print(e)
            }
        }
    }




}
