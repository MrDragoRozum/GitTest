package ru.rozum.gitTest.domain.usecase

import ru.rozum.gitTest.domain.repository.AppRepository
import javax.inject.Inject

class GetRepositoryReadmeUseCase @Inject constructor(private val repository: AppRepository) {
    suspend operator fun invoke(
        ownerName: String,
        repositoryName: String,
        branchName: String
    ): String = repository.getRepositoryReadme(ownerName, repositoryName, branchName)
}