package ru.rozum.gitTest.data.network.api

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import ru.rozum.gitTest.data.network.dto.*


interface ApiService {

    @GET(USER)
    @Headers(BASE_HEADERS)
    suspend fun getUserInfo(@Header(AUTH) token: String): UserInfoDto

    @GET(REPOS_SORTED_PUSHED)
    @Headers(BASE_HEADERS)
    suspend fun getRepositories(@Header(AUTH) token: String): List<RepoDto>

    @GET(REPO)
    @Headers(BASE_HEADERS)
    suspend fun getRepository(@Header(AUTH) token: String, @Path(ID_REPO) id: Long): RepoDetailsDto

    @GET(URL_README_PATHS)
    suspend fun getRepositoryReadme(
        @Path(OWNER_PATH) ownerName: String,
        @Path(REPOSITORY_PATH) repositoryName: String,
        @Path(BRANCH_PATH) branchName: String
    ): String
}

private const val BASE_HEADERS =
    "Accept:application/vnd.github+json X-GitHub-Api-Version:2022-11-28"
private const val AUTH = "Authorization"

private const val USER = "user"
private const val REPOS = "$USER/repos"
private const val SORTED_PUSHED = "sort=pushed"
private const val REPOS_SORTED_PUSHED = "$REPOS?$SORTED_PUSHED"

private const val ID_REPO = "id"
private const val REPO = "repositories/{$ID_REPO}"

private const val OWNER_PATH = "ownerName"
private const val REPOSITORY_PATH = "repositoryName"
private const val BRANCH_PATH = "branchName"
private const val URL_README_PATHS = "{$OWNER_PATH}/{$REPOSITORY_PATH}/{$BRANCH_PATH}/README.md"