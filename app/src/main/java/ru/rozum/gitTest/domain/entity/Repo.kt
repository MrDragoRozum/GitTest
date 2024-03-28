package ru.rozum.gitTest.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Repo(
    val id: String,
    val name: String,
    val description: String,
    val language: String,
    val colorLanguageRGB: String,
    val branch: String,
) : Parcelable