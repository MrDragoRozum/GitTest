package ru.rozum.gitTest.data

import ru.rozum.gitTest.domain.entity.Repo
import ru.rozum.gitTest.domain.entity.RepoDetails
import ru.rozum.gitTest.domain.entity.UserInfo
import ru.rozum.gitTest.domain.repository.AppRepository

class AppRepositoryImpl constructor() : AppRepository {
    override suspend fun getRepositories(): List<Repo> {
        TODO("Not yet implemented")
    }

    override suspend fun getRepository(repoId: String): RepoDetails {
        TODO("Not yet implemented")
    }

    override suspend fun getRepositoryReadme(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String {
        TODO("Not yet implemented")
    }

    override suspend fun signIn(token: String): UserInfo {
        TODO("Not yet implemented")
    }
}