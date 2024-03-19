package ru.rozum.gitTest.data.dto

import kotlinx.serialization.SerialName

data class UserInfoDto(@SerialName("login") val login: String)
