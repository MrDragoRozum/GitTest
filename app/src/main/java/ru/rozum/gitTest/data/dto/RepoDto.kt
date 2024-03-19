package ru.rozum.gitTest.data.dto

import kotlinx.serialization.SerialName

data class RepoDto(
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("language") val language: String,
)