package ru.rozum.gitTest.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.decodeFromStream
import retrofit2.Response
import ru.rozum.gitTest.R
import ru.rozum.gitTest.data.external.ColorJson
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
    private val client: LocalPropertiesClient,
    // TODO вынести Dispatchers сюда
    @ApplicationContext val context: Context
) : AppRepository {

    override suspend fun getToken(): String = client.getToken()

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun getRepositories(): List<Repo> = connect(
        response = apiGitHubService.getRepositories(client.getTokenForGitHub()),
        errorMessageException = "Something error",
    ) { list ->
        list.map { repoDto ->
            withContext(Dispatchers.IO) {
                context.resources.openRawResource(R.raw.colors).use { inputStream ->
                    Json.decodeFromStream<JsonObject>(inputStream)[repoDto.language]?.let {
                        repoDto.colorLanguageRGB = Json.decodeFromJsonElement<ColorJson>(it).color
                    }
                }
                mapper.mapRepoDtoToEntity(repoDto)
            }
        }
    }

    override suspend fun getRepository(repoId: String): RepoDetails = connect(
        response = apiGitHubService.getRepository(client.getTokenForGitHub(), repoId)
    ) { mapper.mapRepoDetailsToEntity(it) }

    override suspend fun signIn(token: String): UserInfo {
        val legalToken = "bearer $token"
        Regex("^bearer ghp_[a-zA-Z0-9]{36}+\$").also {
            if (!legalToken.matches(it)) throw IllegalArgumentException("Invalid token")
        }

        return connect(
            response = apiGitHubService.getUserInfo(legalToken),
            errorMessageException = "information for developers",
            levelErrorMessage = MESSAGE_CODE
        ) {
            client.saveToken(KeyValueStorage(token))
            mapper.mapUserInfoDtoToEntity(it)
        }
    }

    override suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String = connect(
        response = rawGitHubService.getRepositoryReadme(ownerName, repositoryName, branchName),
        levelErrorMessage = CODE
    ) { it }

    private inline fun <T, V> connect(
        response: Response<T>,
        errorMessageException: String = "Connection error",
        levelErrorMessage: LevelException = MESSAGE,
        result: (T) -> V
    ): V {
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

