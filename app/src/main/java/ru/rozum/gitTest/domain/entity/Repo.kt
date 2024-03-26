package ru.rozum.gitTest.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Repo(
    val id: Long,
    val name: String,
    val description: String,
    val language: String,
    val colorLanguageRGB: String,
) : Parcelable