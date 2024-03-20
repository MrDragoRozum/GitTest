package ru.rozum.gitTest.domain.repository

import ru.rozum.gitTest.domain.entity.*

interface AppRepository {
    suspend fun getRepositories(page: Int): List<Repo>
    suspend fun getRepository(repoId: String): RepoDetails
    suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String

    suspend fun signIn(token: String): UserInfo
}