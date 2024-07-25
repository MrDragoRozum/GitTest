package ru.rozum.gitTest.domain.repository

import ru.rozum.gitTest.domain.entity.User

interface UserRepository {
    suspend fun signIn(token: String): User
    fun getToken(): String
}