package ru.rozum.gitTest.data.mapper

import ru.rozum.gitTest.data.network.dto.UserDto
import ru.rozum.gitTest.domain.entity.User

fun UserDto.toEntity(): User = User(login = login)