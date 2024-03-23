package ru.rozum.gitTest.data.repository

import retrofit2.Response
import ru.rozum.gitTest.data.local.*
import ru.rozum.gitTest.data.mapper.AppMapper
import ru.rozum.gitTest.data.network.api.*
import ru.rozum.gitTest.domain.entity.*
import ru.rozum.gitTest.domain.repository.AppRepository
import ru.rozum.gitTest.exception.ConcreteCodeException
import javax.inject.Inject
import ru.rozum.gitTest.data.repository.LevelException.*

class AppRepositoryImpl @Inject constructor(
    private val apiGitHubService: ApiGitHubService,
    private val rawGitHubService: RawGitHubService,
    private val mapper: AppMapper,
    private val client: LocalPropertiesClient
) : AppRepository {

    override suspend fun getToken(): String = client.getToken()

    override suspend fun getRepositories(): List<Repo> = connect(
        response = apiGitHubService.getRepositories(client.getTokenForGitHub()),
        errorMessageException = "Something error",
    ) { answer -> answer.map { mapper.mapRepoDtoToEntity(it) } }

    override suspend fun getRepository(repoId: String): RepoDetails = connect(
        response = apiGitHubService.getRepository(client.getTokenForGitHub(), repoId)
    ) { mapper.mapRepoDetailsToEntity(it) }

    override suspend fun signIn(token: String): UserInfo {
        val legalToken = "bearer $token"
        return connect(
            response = apiGitHubService.getUserInfo(legalToken),
            errorMessageException = "information for developers",
            levelErrorMessage = MESSAGE_CODE,
            additionalException = {
                val regex = Regex("^bearer \\w+$")
                if (!legalToken.matches(regex)) throw IllegalArgumentException("Invalid token")
            }) {
            client.saveToken(KeyValueStorage(token))
            mapper.mapUserInfoDtoToEntity(it)
        }
    }

    override suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String = connect(
        levelErrorMessage = CODE,
        response = rawGitHubService.getRepositoryReadme(ownerName, repositoryName, branchName)
    ) { it }

    private inline fun <T, V> connect(
        response: Response<T>,
        errorMessageException: String = "Connection error",
        levelErrorMessage: LevelException = MESSAGE,
        additionalException: (() -> Unit) = {},
        result: ((T) -> V)
    ): V {
        additionalException.invoke()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) return result.invoke(body)
        }
        when (levelErrorMessage) {
            MESSAGE -> throw RuntimeException(errorMessageException)
            CODE -> throw ConcreteCodeException("${response.code()}")
            MESSAGE_CODE -> {
                throw RuntimeException("Code: ${response.code()}\n$errorMessageException")
            }
        }
    }
}

private enum class LevelException {
    MESSAGE, MESSAGE_CODE, CODE
}

