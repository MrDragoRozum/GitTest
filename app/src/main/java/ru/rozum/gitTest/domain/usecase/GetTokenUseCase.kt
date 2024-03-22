package ru.rozum.gitTest.domain.usecase

import ru.rozum.gitTest.domain.repository.AppRepository
import javax.inject.Inject

class GetTokenUseCase @Inject constructor(private val repository: AppRepository) {
    suspend operator fun invoke(): String = repository.getToken()
}