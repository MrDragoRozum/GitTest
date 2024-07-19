package ru.rozum.gitTest.domain.repository

import ru.rozum.gitTest.domain.entity.*

interface AppRepository {
    suspend fun getRepositories(): List<Repo>
    suspend fun getRepository(repoId: String): RepoDetails
    suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String

    suspend fun signIn(token: String): UserInfo
    fun getToken(): String
}