package ru.rozum.gitTest.data.mapper

import ru.rozum.gitTest.data.network.dto.UserDto
import ru.rozum.gitTest.domain.entity.UserInfo

fun UserDto.toEntity(): UserInfo = UserInfo(login = login)