package ru.rozum.gitTest.data.external

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ColorJson(
    @SerialName("color") val color: String
)
