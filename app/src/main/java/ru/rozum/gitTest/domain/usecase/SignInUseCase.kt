package ru.rozum.gitTest.domain.usecase

import ru.rozum.gitTest.domain.entity.UserInfo
import ru.rozum.gitTest.domain.repository.AppRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val repository: AppRepository) {
    suspend operator fun invoke(token: String): UserInfo = repository.signIn(token)
}