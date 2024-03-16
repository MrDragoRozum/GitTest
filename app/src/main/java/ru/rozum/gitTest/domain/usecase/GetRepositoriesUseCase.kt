package ru.rozum.gitTest.domain.usecase

import ru.rozum.gitTest.domain.entity.Repo
import ru.rozum.gitTest.domain.repository.AppRepository

class GetRepositoriesUseCase(private val repository: AppRepository) {
    suspend operator fun invoke(): List<Repo> = repository.getRepositories()
}