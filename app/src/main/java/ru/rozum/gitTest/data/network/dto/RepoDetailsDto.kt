package ru.rozum.gitTest.data.network.dto

import kotlinx.serialization.SerialName

data class RepoDetailsDto(
    @SerialName("name") val name: String,
    @SerialName("html_url") val htmlUser: String,
    @SerialName("license") val license: String,
    @SerialName("stargazers_count") val starts: Int,
    @SerialName("forks") val forks: Int,
    @SerialName("watchers") val watchers: Int,
    @SerialName("description") val description: String,
    @SerialName("language") val language: String,
)
