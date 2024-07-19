package ru.rozum.gitTest.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String?,
    @SerialName("language") val language: String?,
    @SerialName("default_branch") val branch: String,
    var colorLanguageRGB: String = "#FFFFFF",
    // TODO: DTO не должен хранить в себе цвета, создать отдельую сущность для этого!
)