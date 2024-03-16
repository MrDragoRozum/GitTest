package ru.rozum.gitTest.domain.usecase

import ru.rozum.gitTest.domain.repository.AppRepository

class GetRepositoryReadmeUseCase(private val repository: AppRepository) {
    suspend operator fun invoke(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String = repository.getRepositoryReadme(ownerName, repositoryName, branchName)
}