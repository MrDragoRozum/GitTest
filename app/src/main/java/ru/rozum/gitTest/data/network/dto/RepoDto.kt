package ru.rozum.gitTest.data.network.dto

import kotlinx.serialization.SerialName

data class RepoDto(val repositories: List<RepoDetailsDto>)