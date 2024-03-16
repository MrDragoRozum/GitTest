package ru.rozum.gitTest.domain.entity

data class RepoDetails(
    val name: String,
    val htmlUser: String,
    val license: String,
    val starts: Int,
    val forks: Int,
    val watchers: Int,
)
