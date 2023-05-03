package jg.coursework.customheroesapp.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jg.coursework.customheroesapp.data.api.ApiHelper
import jg.coursework.customheroesapp.data.model.AuthResponse
import jg.coursework.customheroesapp.data.model.UserLoginRequest
import jg.coursework.customheroesapp.data.model.UserRegisterRequest
import jg.coursework.customheroesapp.util.PreferenceManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val apiHelper: ApiHelper, private val preferenceManager: PreferenceManager) : ViewModel() {


    sealed class AuthState {
        object Loading : AuthState()
        data class Success(val authResponse: AuthResponse) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    private val _state = MutableStateFlow<AuthState>(AuthState.Loading)
    val state: StateFlow<AuthState> = _state

    fun authenticateUser(request: UserLoginRequest) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val authResponse = apiHelper.auth(request)
                preferenceManager.setTokens(authResponse)
                _state.value = AuthState.Success(authResponse)
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun registerUser(request: UserRegisterRequest) {
        viewModelScope.launch {
            _state.value = AuthState.Loading
            try {
                val authResponse = apiHelper.register(request)
                preferenceManager.setTokens(authResponse)
                _state.value = AuthState.Success(authResponse)
            } catch (e: Exception) {
                _state.value = AuthState.Error(e.message ?: "An error occurred")
            }
        }
    }

    fun getUserInfo(){
        viewModelScope.launch {
            try {
                val user = apiHelper.getMe()
                preferenceManager.setUser(user)
            }
            catch (e: Exception) {

            }
        }
    }

}