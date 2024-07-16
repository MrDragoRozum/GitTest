package ru.rozum.gitTest.data.mapper

import ru.rozum.gitTest.data.network.dto.UserInfoDto
import ru.rozum.gitTest.domain.entity.UserInfo

fun UserInfoDto.toEntity(): UserInfo = UserInfo(login = login)