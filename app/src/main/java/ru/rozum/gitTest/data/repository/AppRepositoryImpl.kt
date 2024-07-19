package ru.rozum.gitTest.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import ru.rozum.gitTest.R
import ru.rozum.gitTest.data.local.KeyValueStorage
import ru.rozum.gitTest.data.formatter.getFormattedReadmeWithLinksImage
import ru.rozum.gitTest.data.mapper.toEntity
import ru.rozum.gitTest.data.network.api.ApiGitHubService
import ru.rozum.gitTest.data.repository.Level.CODE
import ru.rozum.gitTest.data.repository.Level.MESSAGE
import ru.rozum.gitTest.data.repository.Level.MESSAGE_CODE
import ru.rozum.gitTest.data.repository.entity.LevelException
import ru.rozum.gitTest.di.RegexLegalTokenQualifier
import ru.rozum.gitTest.domain.entity.Repo
import ru.rozum.gitTest.domain.entity.RepoDetails
import ru.rozum.gitTest.domain.entity.UserInfo
import ru.rozum.gitTest.domain.repository.AppRepository
import java.net.ConnectException
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val apiGitHubService: ApiGitHubService,
    private val client: KeyValueStorage,
    private val dispatcherIO: CoroutineDispatcher,
    @RegexLegalTokenQualifier private val regexLegalToken: Regex,
    @ApplicationContext val context: Context
) : AppRepository {

    override fun getToken(): String = client.getToken()

    override suspend fun getRepositories(): List<Repo> = executeRequest(
        response = withContext(dispatcherIO) {
            apiGitHubService.getRepositories()
        },
        levelException = LevelException(
            context.getString(R.string.something_error),
            MESSAGE
        ),
    ) { list ->
        list.map {
            withContext(dispatcherIO) {
                it.toEntity(context)
            }
        }
    }

    override suspend fun getRepository(repoId: String): RepoDetails = executeRequest(
        response = withContext(dispatcherIO) {
            apiGitHubService.getRepository(repoId)
        },
        levelException = LevelException(
            context.getString(R.string.connection_error),
            MESSAGE
        )
    ) {
        it.toEntity()
    }

    override suspend fun signIn(token: String): UserInfo {
        val legalToken = "bearer $token"
        regexLegalToken.also {
            if (!legalToken.matches(it))
                throw IllegalArgumentException(context.getString(R.string.invalid_token))
        }

        return executeRequest(
            response = withContext(dispatcherIO) {
                apiGitHubService.signIn(legalToken)
            },
            levelException = LevelException(
                context.getString(R.string.info_for_dev),
                MESSAGE_CODE
            )
        ) {
            client.saveToken(token)
            it.toEntity()
        }
    }

    override suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String = executeRequest(
        response = apiGitHubService.getRepositoryReadme(ownerName, repositoryName, branchName),
        levelException = LevelException(
            context.getString(R.string.connection_error),
            CODE
        )
    ) { readme ->
        StringBuilder(readme).getFormattedReadmeWithLinksImage(
            ownerName,
            repositoryName,
            branchName
        )
    }

    private inline fun <T, V> executeRequest(
        response: Response<T>,
        levelException: LevelException,
        result: (T) -> V
    ): V {
        if (response.isSuccessful) return result(response.body()!!)
        returnNetworkError(response, levelException)
    }

    private fun <T> returnNetworkError(
        response: Response<T>,
        levelException: LevelException
    ): Nothing {
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

enum class Level {
    MESSAGE, MESSAGE_CODE, CODE
}