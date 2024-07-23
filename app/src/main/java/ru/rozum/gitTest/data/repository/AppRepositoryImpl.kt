package ru.rozum.gitTest.data.repository

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.rozum.gitTest.data.formatter.Readme
import ru.rozum.gitTest.data.local.KeyValueStorage
import ru.rozum.gitTest.data.mapper.toEntity
import ru.rozum.gitTest.data.network.api.ApiGitHubService
import ru.rozum.gitTest.data.network.util.ExecutorRequest
import ru.rozum.gitTest.domain.entity.Repo
import ru.rozum.gitTest.domain.entity.RepoDetails
import ru.rozum.gitTest.domain.entity.UserInfo
import ru.rozum.gitTest.domain.repository.AppRepository
import javax.inject.Inject

class AppRepositoryImpl @Inject constructor(
    private val apiGitHubService: ApiGitHubService,
    private val client: KeyValueStorage,
    @ApplicationContext val context: Context
) : AppRepository {

    override fun getToken(): String = client.getToken()

    override suspend fun getRepositories(): List<Repo> = ExecutorRequest.execute(
        response = apiGitHubService.getRepositories()
    ) { repos ->
        repos.map { repo ->
            repo.toEntity(context)
        }
    }

    override suspend fun getRepository(repoId: String): RepoDetails = ExecutorRequest.execute(
        response = apiGitHubService.getRepository(repoId)
    ) { repo ->
        repo.toEntity()
    }

    override suspend fun signIn(token: String): UserInfo = ExecutorRequest.execute(
        response = apiGitHubService.signIn(token)
    ) { user ->
        client.saveToken(token)
        user.toEntity()
    }

    override suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String = ExecutorRequest.executeReadme(
        response = apiGitHubService.getRepositoryReadme(ownerName, repositoryName, branchName)
    ) { readme ->
        Readme.getFormattedWithLinksImage(
            value = StringBuilder(readme),
            ownerName = ownerName,
            repositoryName = repositoryName,
            branchName = branchName
        )
    }
}