package ru.rozum.gitTest.domain.repository

import ru.rozum.gitTest.domain.entity.UserInfo

interface UserRepository {
    suspend fun signIn(token: String): UserInfo
    fun getToken(): String
}