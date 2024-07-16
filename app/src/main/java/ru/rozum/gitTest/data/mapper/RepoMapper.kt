package ru.rozum.gitTest.data.mapper

import ru.rozum.gitTest.data.network.dto.RepoDetailsDto
import ru.rozum.gitTest.data.network.dto.RepoDto
import ru.rozum.gitTest.domain.entity.Repo
import ru.rozum.gitTest.domain.entity.RepoDetails

fun RepoDto.toEntity(): Repo = Repo(
    id = id,
    name = name,
    description = description ?: REPO_WITHOUT_IT,
    language = language ?: REPO_WITHOUT_IT,
    colorLanguageRGB = colorLanguageRGB,
    branch = branch
)

fun RepoDetailsDto.toEntity(): RepoDetails = RepoDetails(
    id = id,
    name = name,
    htmlUser = htmlUser,
    license = license ?: REPO_WITHOUT_IT,
    stars = stars,
    forks = forks,
    watchers = watchers,
    description = description ?: REPO_WITHOUT_IT,
    language = language ?: REPO_WITHOUT_IT
)

private const val REPO_WITHOUT_IT = ""