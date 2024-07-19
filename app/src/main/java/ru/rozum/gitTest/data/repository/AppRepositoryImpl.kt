package ru.rozum.gitTest.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
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
import ru.rozum.gitTest.data.network.util.ExecutorRequest
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val apiGitHubService: ApiGitHubService,
    private val client: KeyValueStorage,
    private val dispatcherIO: CoroutineDispatcher,
    private val executorRequest: ExecutorRequest,
    @RegexLegalTokenQualifier private val regexLegalToken: Regex,
    @ApplicationContext val context: Context
) : AppRepository {

    override fun getToken(): String = client.getToken()

    override suspend fun getRepositories(): List<Repo> = executorRequest.execute(
        response = withContext(dispatcherIO) {
            apiGitHubService.getRepositories()
        },
        levelException = LevelException(
            context.getString(R.string.something_error),
            MESSAGE
        ),
    ) { repos ->
        repos.map {
            withContext(dispatcherIO) {
                it.toEntity(context)
            }
        }
    }

    override suspend fun getRepository(repoId: String): RepoDetails = executorRequest.execute(
        response = withContext(dispatcherIO) {
            apiGitHubService.getRepository(repoId)
        },
        levelException = LevelException(
            context.getString(R.string.connection_error),
            MESSAGE
        )
    ) { repo ->
        repo.toEntity()
    }

    override suspend fun signIn(token: String): UserInfo {
        val legalToken = "bearer $token"
        regexLegalToken.also {
            if (!legalToken.matches(it))
                throw IllegalArgumentException(context.getString(R.string.invalid_token))
        }

        return executorRequest.execute(
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
    ): String = executorRequest.execute(
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
}

enum class Level {
    MESSAGE, MESSAGE_CODE, CODE
}