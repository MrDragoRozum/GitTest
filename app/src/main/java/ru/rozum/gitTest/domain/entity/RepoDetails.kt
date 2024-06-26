package ru.rozum.gitTest.domain.entity

data class RepoDetails(
    val id: String,
    val name: String,
    val htmlUser: String,
    val license: String,
    val stars: Int,
    val forks: Int,
    val watchers: Int,
    val description: String,
    val language: String,
)