package ru.rozum.gitTest.data.network.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import ru.rozum.gitTest.data.network.dto.RepoDetailsDto
import ru.rozum.gitTest.data.network.dto.RepoDto
import ru.rozum.gitTest.data.network.dto.UserInfoDto

interface ApiGitHubService {

    @GET("user")
    suspend fun signIn(@Header("Authorization") token: String): Response<UserInfoDto>

    @GET("repositories/{id}")
    suspend fun getRepository(
        @Path("id") id: String
    ): Response<RepoDetailsDto>

    @GET("user/repos?sort=pushed&per_page=10")
    suspend fun getRepositories(): Response<List<RepoDto>>

    @GET("https://raw.githubusercontent.com/{ownerName}/{repositoryName}/{branchName}/README.md")
    suspend fun getRepositoryReadme(
        @Path("ownerName") ownerName: String,
        @Path("repositoryName") repositoryName: String,
        @Path("branchName") branchName: String
    ): Response<String>
}