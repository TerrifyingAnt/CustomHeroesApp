package jg.coursework.customheroesapp.util

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import jg.coursework.customheroesapp.data.model.AuthResponse
import jg.coursework.customheroesapp.data.model.UserRegisterRequest
import kotlinx.coroutines.flow.first

class PreferenceManager(context: Context) {
    private val dataStore = context.createDataStore(name = "user_prefs")

    companion object {
        val ACCESS_TOKEN = preferencesKey<String>("access_token")
        val REFRESH_TOKEN = preferencesKey<String>("refresh_token")
        val USER = preferencesKey<String>("user_info")
    }

    suspend fun setTokens(tokens: AuthResponse) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = tokens.accessToken
            preferences[REFRESH_TOKEN] = tokens.refreshToken
        }
    }

    suspend fun setUser(user: UserRegisterRequest) {
        dataStore.edit { preferences ->
            preferences[USER] = "${user.login},${user.password},${user.type},${user.phoneNumber},${user.avatarSource}"
        }
    }

    suspend fun getAccessToken(): String? {
        val preferences = dataStore.data.first()
        return preferences[ACCESS_TOKEN]
    }

    suspend fun getUser(): UserRegisterRequest? {
        val preferences = dataStore.data.first()
        val userInfo = preferences[USER] ?: return null
        val userInfoList = userInfo.split(",")
        return UserRegisterRequest(
            login = userInfoList[0],
            password = userInfoList[1],
            type = userInfoList[2],
            phoneNumber = userInfoList[3],
            avatarSource = userInfoList[4]
        )
    }


}