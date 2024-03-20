package ru.rozum.gitTest.data.network.api

import retrofit2.http.GET
import retrofit2.http.Path

interface RawGitHubService {
    @GET(URL_README_PATHS)
    suspend fun getRepositoryReadme(
        @Path(OWNER_PATH) ownerName: String,
        @Path(REPOSITORY_PATH) repositoryName: String,
        @Path(BRANCH_PATH) branchName: String
    ): String
}

private const val OWNER_PATH = "ownerName"
private const val REPOSITORY_PATH = "repositoryName"
private const val BRANCH_PATH = "branchName"
private const val URL_README_PATHS = "{$OWNER_PATH}/{$REPOSITORY_PATH}/{$BRANCH_PATH}/README.md"