package ru.rozum.gitTest.data.repository

import ru.rozum.gitTest.data.local.*
import ru.rozum.gitTest.data.mapper.AppMapper
import ru.rozum.gitTest.data.network.api.*
import ru.rozum.gitTest.domain.entity.*
import ru.rozum.gitTest.domain.repository.AppRepository
import ru.rozum.gitTest.exception.UnauthorizedException
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val apiGitHubService: ApiGitHubService,
    private val rawGitHubService: RawGitHubService,
    private val mapper: AppMapper,
    private val client: LocalPropertiesClient
) : AppRepository {
    override suspend fun getRepositories(page: Int): List<Repo> =
        apiGitHubService.getRepositories(client.getToken(), page).map {
            mapper.mapRepoDtoToEntity(it)
        }

    override suspend fun getRepository(repoId: String): RepoDetails =
        mapper.mapRepoDetailsToEntity(
            apiGitHubService.getRepository(client.getToken(), repoId)
        )

    override suspend fun signIn(token: String): UserInfo {
        val response = apiGitHubService.getUserInfo(token)
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                client.saveToken(KeyValueStorage(token))
                return mapper.mapUserInfoDtoToEntity(body)
            }
        }
        throw UnauthorizedException("Auth is failed")
    }

    override suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String = rawGitHubService.getRepositoryReadme(ownerName, repositoryName, branchName)
}