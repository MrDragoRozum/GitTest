package ru.rozum.gitTest.data.repository

import retrofit2.Response
import ru.rozum.gitTest.data.local.*
import ru.rozum.gitTest.data.mapper.AppMapper
import ru.rozum.gitTest.data.network.api.*
import ru.rozum.gitTest.domain.entity.*
import ru.rozum.gitTest.domain.repository.AppRepository
import ru.rozum.gitTest.exception.InvalidTokenException
import ru.rozum.gitTest.exception.ConnectionException
import java.net.ConnectException
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

    override suspend fun getRepositories(): List<Repo> =
        connect(
            response = apiGitHubService.getRepositories(client.getToken()),
            errorMessageException = "Something error",
            isAddingCodeError = false,
        ) { answer -> answer.map { mapper.mapRepoDtoToEntity(it) } }

    override suspend fun getRepository(repoId: String): RepoDetails =
        mapper.mapRepoDetailsToEntity(
            apiGitHubService.getRepository(client.getToken(), repoId)
        )

    override suspend fun signIn(token: String): UserInfo = connect(
        response = apiGitHubService.getUserInfo(token),
        errorMessageException = "information for developers",
        isAddingCodeError = true,
        {
            val regex = Regex("^\\w+$")
            if (!token.matches(regex)) throw InvalidTokenException("Invalid token")
        }) { mapper.mapUserInfoDtoToEntity(it) }

//    {
    //        if (!token.matches(Regex("^\\w+$"))) throw InvalidTokenException("Invalid token")
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


    override suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String = rawGitHubService.getRepositoryReadme(ownerName, repositoryName, branchName)

    private inline fun <T, V> connect(
        response: Response<T>,
        errorMessageException: String,
        isAddingCodeError: Boolean,
        vararg otherExceptions: (() -> Unit) = arrayOf({}),
        result: (T) -> V
    ): V {
        otherExceptions.forEach { it.invoke() }
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) return result.invoke(body)
        }
        if (isAddingCodeError) throw RuntimeException(errorMessageException)
        else throw RuntimeException("${response.code()}\n$errorMessageException")
    }
}