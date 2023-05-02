package jg.coursework.customheroesapp.util

import android.content.Context
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import jg.coursework.customheroesapp.data.model.AuthResponse
import jg.coursework.customheroesapp.data.model.User
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

    suspend fun setUser(user: User) {
        dataStore.edit { preferences ->
            preferences[USER] = "${user.id},${user.login},${user.password},${user.type},${user.phoneNumber},${user.avatarSource}"
        }
    }

    suspend fun getAccessToken(): String? {
        val preferences = dataStore.data.first()
        return preferences[ACCESS_TOKEN]
    }

    suspend fun getUser(): User? {
        val preferences = dataStore.data.first()
        val userInfo = preferences[USER] ?: return null
        val userInfoList = userInfo.split(",")
        return User(
            id = userInfoList[0].toInt(),
            login = userInfoList[1],
            password = userInfoList[2],
            type = userInfoList[3],
            phoneNumber = userInfoList[4],
            avatarSource = userInfoList[5]
        )
    }


}