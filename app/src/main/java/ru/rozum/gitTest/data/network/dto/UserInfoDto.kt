package ru.rozum.gitTest.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(@SerialName("login") val login: String)
