package ru.rozum.gitTest.data.network.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import ru.rozum.gitTest.data.network.dto.*

interface ApiService {

    @GET(USER)
    @Headers(BASE_HEADERS)
    suspend fun getUserInfo(@Header(AUTH) token: String): Response<UserInfoDto>

    @GET(REPOS)
    @Headers(BASE_HEADERS)
    suspend fun getRepositories(@Header(AUTH) token: String): Response<List<RepoDto>>

    @GET(URL_README_PATHS)
    suspend fun getRepositoryReadme(
        @Path(OWNER_PATH) ownerName: String,
        @Path(REPOSITORY_PATH) repositoryName: String,
        @Path(BRANCH_PATH) branchName: String
    ): String
}

private const val USER = "user"
private const val BASE_HEADERS =
    """Accept: application/vnd.github+json X-GitHub-Api-Version: 2022-11-28"""
private const val AUTH = "Authorization: Bearer"
private const val REPOS = "$USER/repos"

private const val URL_README = "https://raw.githubusercontent.com/"
private const val OWNER_PATH = "ownerName"
private const val REPOSITORY_PATH = "repositoryName"
private const val BRANCH_PATH = "repositoryName"
private const val URL_README_PATHS = "$URL_README{$OWNER_PATH}/{$REPOSITORY_PATH}/{$BRANCH_PATH}"