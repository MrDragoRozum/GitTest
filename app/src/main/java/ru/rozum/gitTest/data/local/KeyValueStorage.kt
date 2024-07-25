package ru.rozum.gitTest.data.local

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyValueStorage @Inject constructor(private val sharedPreferences: SharedPreferences) {

    val token: String
        get() = sharedPreferences.getString(KEY_USER, null) ?: TOKEN_NO_FOUND_EMPTY

    fun saveToken(key: String) = sharedPreferences.edit().putString(KEY_USER, key).apply()

    private companion object {
        const val KEY_USER = "KEY_USER"
        const val TOKEN_NO_FOUND_EMPTY = ""
    }
}

