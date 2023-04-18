package jg.coursework.customheroesapp.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jg.coursework.customheroesapp.data.api.ApiHelper
import jg.coursework.customheroesapp.util.PreferenceManager


class AuthViewModelFactory(private val apiHelper: ApiHelper, private val preferenceManager: PreferenceManager) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(apiHelper, preferenceManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}