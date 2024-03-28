package ru.rozum.gitTest.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(val login: String)