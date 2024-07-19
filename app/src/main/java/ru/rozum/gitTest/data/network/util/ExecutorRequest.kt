package ru.rozum.gitTest.data.network.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import ru.rozum.gitTest.R
import ru.rozum.gitTest.data.repository.Level.CODE
import ru.rozum.gitTest.data.repository.Level.MESSAGE
import ru.rozum.gitTest.data.repository.Level.MESSAGE_CODE
import ru.rozum.gitTest.data.repository.entity.LevelException
import java.net.ConnectException
import javax.inject.Inject

class ExecutorRequest @Inject constructor(
    @ApplicationContext val context: Context
) {
    inline fun <T, V> execute(
        response: Response<T>,
        levelException: LevelException,
        result: (T) -> V
    ): V {
        if (response.isSuccessful) return result(response.body()!!)

        val code = response.code()
        val message = when (levelException.level) {
            MESSAGE -> levelException.message
            CODE -> "$code"
            MESSAGE_CODE -> context.getString(
                R.string.message_details,
                code,
                levelException.message
            )
        }
        throw ConnectException(message)
    }
}