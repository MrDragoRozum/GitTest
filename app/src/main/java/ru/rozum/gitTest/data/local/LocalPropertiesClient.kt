package ru.rozum.gitTest.data.local

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.rozum.gitTest.exception.NoTokenFoundException
import javax.inject.Inject

class LocalPropertiesClient @Inject constructor(private val sharedPreferences: SharedPreferences) {

    suspend fun saveToken(key: KeyValueStorage) {
        withContext(Dispatchers.IO) {
            sharedPreferences.edit().putString(KEY_USER, key.authToken).apply()
        }
    }

    suspend fun getToken(): String = withContext(Dispatchers.IO) {
        sharedPreferences.getString(KEY_USER, null)
            ?: throw NoTokenFoundException("Token not found")
    }
}

private const val KEY_USER = "KEY_USER"