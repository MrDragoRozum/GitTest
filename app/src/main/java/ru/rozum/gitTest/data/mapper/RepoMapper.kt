package ru.rozum.gitTest.data.mapper

import android.content.Context
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.decodeFromStream
import ru.rozum.gitTest.R
import ru.rozum.gitTest.data.external.ColorJson
import ru.rozum.gitTest.data.network.dto.RepoDetailsDto
import ru.rozum.gitTest.data.network.dto.RepoDto
import ru.rozum.gitTest.domain.entity.Repo
import ru.rozum.gitTest.domain.entity.RepoDetails

fun RepoDto.toEntity(context: Context): Repo = Repo(
    id = id,
    name = name,
    description = isExist(description),
    language = isExist(language),
    colorLanguageRGB = getColor(context),
    branch = branch
)

private fun RepoDto.getColor(context: Context): String = findColor(context) ?: DEFAULT_COLOR

@OptIn(ExperimentalSerializationApi::class)
private fun RepoDto.findColor(
    context: Context
): String? = context.resources.openRawResource(R.raw.colors).use { inputStream ->
    Json.decodeFromStream<JsonObject>(inputStream)[language]?.let {
        Json.decodeFromJsonElement<ColorJson>(it).color
    }
}

fun RepoDetailsDto.toEntity(): RepoDetails = RepoDetails(
    id = id,
    name = name,
    htmlUser = htmlUrl,
    license = isExist(license),
    stars = stars,
    forks = forks,
    watchers = watchers,
    description = isExist(description),
    language = isExist(language)
)

private fun isExist(value: String?): String = value ?: REPO_WITHOUT_IT

private const val REPO_WITHOUT_IT = ""
private const val DEFAULT_COLOR = "#FFFFFF"
