package ru.rozum.gitTest.domain.usecase

import ru.rozum.gitTest.domain.entity.RepoDetails
import ru.rozum.gitTest.domain.repository.AppRepository

class GetRepositoryUseCase(private val repository: AppRepository) {
    suspend operator fun invoke(repoId: String): RepoDetails = repository.getRepository(repoId)
}