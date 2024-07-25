package ru.rozum.gitTest.data.network.util

import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.onSuccess
import com.skydoves.sandwich.retrofit.statusCode
import ru.rozum.gitTest.exception.ClientConnectionException
import ru.rozum.gitTest.exception.NoReadmeException
import ru.rozum.gitTest.exception.ServerConnectionException

object ExecutorRequest {
    inline fun <D, E> execute(
        response: ApiResponse<D>,
        result: (D) -> E
    ): E {
        var answer: D? = null

        response.onSuccess {
            answer = data
        }.onError {
            throw ServerConnectionException(message = toString())
        }.onException {
            throw ClientConnectionException(message = toString())
        }

        val currentAnswer = answer ?: error("answer (D) не может быть null-ом!")
        return result(currentAnswer)
    }

    inline fun executeReadme(
        response: ApiResponse<String>,
        result: (String) -> String
    ): String {
        response.onError {
            if (isReadmeNotExist())
                throw NoReadmeException(message = toString())
        }

        val answer = execute(response, result)
        return answer
    }

    fun ApiResponse.Failure.Error.isReadmeNotExist(): Boolean = statusCode.code == 404
}