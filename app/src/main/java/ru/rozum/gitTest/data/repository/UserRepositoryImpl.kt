package ru.rozum.gitTest.data.repository

import ru.rozum.gitTest.data.local.KeyValueStorage
import ru.rozum.gitTest.data.mapper.toEntity
import ru.rozum.gitTest.data.network.api.ApiGitHubService
import ru.rozum.gitTest.data.network.util.ExecutorRequest
import ru.rozum.gitTest.domain.entity.UserInfo
import ru.rozum.gitTest.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val client: KeyValueStorage,
    private val apiGitHubService: ApiGitHubService
) : UserRepository {

    override suspend fun signIn(token: String): UserInfo = ExecutorRequest.execute(
        response = apiGitHubService.signIn(token)
    ) { user ->
        client.saveToken(token)
        user.toEntity()
    }

    override fun getToken(): String = client.getToken()
}