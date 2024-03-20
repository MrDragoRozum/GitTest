package ru.rozum.gitTest.domain.usecase

import ru.rozum.gitTest.domain.entity.RepoDetails
import ru.rozum.gitTest.domain.repository.AppRepository
import javax.inject.Inject

class GetRepositoryUseCase @Inject constructor(private val repository: AppRepository) {
    suspend operator fun invoke(repoId: String): RepoDetails = repository.getRepository(repoId)
}