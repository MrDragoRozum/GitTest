package ru.rozum.gitTest.data.local

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class KeyValueStorage @Inject constructor(private val sharedPreferences: SharedPreferences) {

    suspend fun saveToken(key: String) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().putString(KEY_USER, key).apply()
        }
    }

    suspend fun getToken(): String = withContext(Dispatchers.IO) {
        sharedPreferences.getString(KEY_USER, null) ?: TOKEN_NO_FOUND_EMPTY
    }

    suspend fun getTokenForGitHub() : String = "bearer ${getToken()}"

    private companion object {
        const val KEY_USER = "KEY_USER"
        const val TOKEN_NO_FOUND_EMPTY = ""
    }
}

