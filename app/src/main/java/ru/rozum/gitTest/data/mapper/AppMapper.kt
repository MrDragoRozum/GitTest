package ru.rozum.gitTest.data.mapper

import ru.rozum.gitTest.data.network.dto.*
import ru.rozum.gitTest.domain.entity.*

class AppMapper {

    fun mapUserInfoDtoToEntity(dto: UserInfoDto): UserInfo = UserInfo(login = dto.login)
    fun mapRepoDtoToEntity(dto: RepoDto): Repo = Repo(
        name = dto.name,
        description = dto.description,
        language = dto.language
    )

    fun mapRepoDetailsToEntity(dto: RepoDetailsDto): RepoDetails = RepoDetails(
        id = dto.id,
        name = dto.name,
        htmlUser = dto.htmlUser,
        license = dto.license,
        starts = dto.starts,
        forks = dto.forks,
        watchers = dto.watchers,
        description = dto.description,
        language = dto.language,
        branch = dto.branch
    )
}