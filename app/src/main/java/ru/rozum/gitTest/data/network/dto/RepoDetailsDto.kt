package ru.rozum.gitTest.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepoDetailsDto(
    val id: String,
    val name: String,
    @SerialName("html_url") val htmlUser: String,
    val license: String?,
    @SerialName("stargazers_count") val stars: Int,
    val forks: Int,
    val watchers: Int,
    val description: String?,
    val language: String?,
)