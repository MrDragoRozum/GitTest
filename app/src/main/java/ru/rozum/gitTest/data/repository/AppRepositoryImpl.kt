package ru.rozum.gitTest.data.repository

import retrofit2.Response
import ru.rozum.gitTest.data.local.*
import ru.rozum.gitTest.data.mapper.AppMapper
import ru.rozum.gitTest.data.network.api.*
import ru.rozum.gitTest.domain.entity.*
import ru.rozum.gitTest.domain.repository.AppRepository
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val apiGitHubService: ApiGitHubService,
    private val rawGitHubService: RawGitHubService,
    private val mapper: AppMapper,
    private val client: LocalPropertiesClient
) : AppRepository {
//    override suspend fun getRepositories(): List<Repo> {
//        val response = apiGitHubService.getRepositories(client.getToken())
//        if (response.isSuccessful) {
//            val body = response.body()
//            if (body != null) {
//                return body.map { mapper.mapRepoDtoToEntity(it) }
//            }
//        }
//        throw ConnectionException("${response.code()}\ninformation for developers")
//    }

    //    override suspend fun getRepository(repoId: String): RepoDetails =
//        mapper.mapRepoDetailsToEntity(
//            apiGitHubService.getRepository(client.getToken(), repoId)
//        )

    //    {         if (!token.matches(Regex("^\\w+$"))) throw InvalidTokenException("Invalid token")
//        val response = apiGitHubService.getUserInfo(token)
//        if (response.isSuccessful) {
//            val body = response.body()
//            if (body != null) {
//                client.saveToken(KeyValueStorage(token))
//                return mapper.mapUserInfoDtoToEntity(body)
//            }
//        }
//        throw ConnectionException("${response.code()}\ninformation for developers")
//    }

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
            isAddingCodeErrorInMessage = true,
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
        response = rawGitHubService.getRepositoryReadme(ownerName, repositoryName, branchName)
    ) { it }

    private inline fun <T, V> connect(
        response: Response<T>,
        errorMessageException: String = "Connection error",
        isAddingCodeErrorInMessage: Boolean = false,
        additionalException: (() -> Unit) = {},
        result: ((T) -> V)
    ): V {
        additionalException.invoke()
        if (response.isSuccessful) {
            throw Error()
            val body = response.body()
            if (body != null) return result.invoke(body)
        }
        if (isAddingCodeErrorInMessage) throw RuntimeException(
            "Code: ${response.code()}\n$errorMessageException"
        ) else throw RuntimeException(errorMessageException)
    }
}