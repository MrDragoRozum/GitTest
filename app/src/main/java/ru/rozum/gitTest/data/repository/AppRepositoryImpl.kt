package ru.rozum.gitTest.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.decodeFromStream
import retrofit2.Response
import ru.rozum.gitTest.R
import ru.rozum.gitTest.data.external.ColorJson
import ru.rozum.gitTest.data.local.KeyValueStorage
import ru.rozum.gitTest.data.mapper.toEntity
import ru.rozum.gitTest.data.network.api.ApiGitHubService
import ru.rozum.gitTest.data.network.dto.RepoDto
import ru.rozum.gitTest.data.repository.Level.CODE
import ru.rozum.gitTest.data.repository.Level.MESSAGE
import ru.rozum.gitTest.data.repository.Level.MESSAGE_CODE
import ru.rozum.gitTest.data.repository.entity.LevelException
import ru.rozum.gitTest.di.RegexLegalTokenQualifier
import ru.rozum.gitTest.di.RegexParsingImagesFromFolderQualifier
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
    @RegexParsingImagesFromFolderQualifier private val regexParsingImagesFromFolder: Regex,
    @ApplicationContext val context: Context
) : AppRepository {

    override fun getToken(): String = client.getToken()

    override suspend fun getRepositories(): List<Repo> = connect(
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
                setColorLanguageRGBInRepoDto(it)
                it.toEntity()
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun setColorLanguageRGBInRepoDto(repoDto: RepoDto) {
        context.resources.openRawResource(R.raw.colors).use { inputStream ->
            Json.decodeFromStream<JsonObject>(inputStream)[repoDto.language]?.let {
                repoDto.colorLanguageRGB = Json.decodeFromJsonElement<ColorJson>(it).color
            }
        }
    }

    override suspend fun getRepository(repoId: String): RepoDetails = connect(
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

        return connect(
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
    ): String = connect(
        response = apiGitHubService.getRepositoryReadme(ownerName, repositoryName, branchName),
        levelException = LevelException(
            context.getString(R.string.connection_error),
            CODE
        )
    ) { readme ->
        val builder = StringBuilder(readme)
        regexParsingImagesFromFolder
            .findAll(builder)
            .map { it.value }
            .forEach { foundLinkImage ->
                addURLForLinkImage(builder, foundLinkImage, ownerName, repositoryName, branchName)
            }
        builder.toString()
    }

    private fun addURLForLinkImage(
        builder: StringBuilder,
        foundLinkImage: String,
        ownerName: String,
        repositoryName: String,
        branchName: String
    ) {
        builder.replace(
            Regex(foundLinkImage), URI_RAW_GITHUB_WITH_FORMATTERS.format(
                ownerName,
                repositoryName,
                branchName,
                foundLinkImage
            )
        ).also { result ->
            builder.setRange(0, result.length, result)
        }
    }

    private inline fun <T, V> connect(
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

    private companion object {
        const val URI_RAW_GITHUB_WITH_FORMATTERS = "https://raw.githubusercontent.com/%s/%s/%s/%s"
    }
}

enum class Level {
    MESSAGE, MESSAGE_CODE, CODE
}