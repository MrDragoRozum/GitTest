package ru.rozum.gitTest.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.rozum.gitTest.data.repository.*
import ru.rozum.gitTest.domain.repository.*

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {

    @Binds
    @ApplicationScope
    fun bindAppRepository(impl: AppRepositoryImpl): AppRepository
}