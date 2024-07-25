package ru.rozum.gitTest.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(val login: String) : Parcelable