package ru.rozum.gitTest.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDetailsDto(
    @SerialName("id") val id: String,
    @SerialName("name") val name: String,
    @SerialName("html_url") val htmlUser: String,
    @SerialName("license") val license: String?,
    @SerialName("stargazers_count") val starts: Int,
    @SerialName("forks") val forks: Int,
    @SerialName("watchers") val watchers: Int,
    @SerialName("description") val description: String?,
    @SerialName("language") val language: String?,
    @SerialName("default_branch") val branch: String,
)
