package ru.rozum.gitTest.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDto(
    val id: String,
    val name: String,
    val description: String?,
    val language: String?,
    @SerialName("default_branch") val branch: String,
    var colorLanguageRGB: String = "#FFFFFF",
)