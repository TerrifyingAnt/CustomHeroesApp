package jg.coursework.customheroesapp.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jg.coursework.customheroesapp.data.api.ApiHelper
import jg.coursework.customheroesapp.data.model.Message
import jg.coursework.customheroesapp.util.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessageViewModel(private val apiHelper: ApiHelper) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages


    fun getMessages() {
        viewModelScope.launch {
            try {
                // TODO
                //val accessToken = "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJ4ZEB4ZC54ZCIsInJvbGVzIjoiQ1VTVE9NRVIiLCJpYXQiOjE2ODE2ODA2NjEsImV4cCI6MTY4MTY4NDI2MX0.Xl-ReUnwIvEcSadMA8RBuRwxuuGIwdmW6ZEs3nZC1sgVDAgSn-F5bt0YrFXtaaQQ"
                val messages = apiHelper.getMessages()
                _messages.value = messages
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
