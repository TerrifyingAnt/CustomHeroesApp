package jg.coursework.customheroesapp.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jg.coursework.customheroesapp.data.api.ApiHelper

class MessageViewModelFactory(private val apiHelper: ApiHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MessageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MessageViewModel(apiHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
