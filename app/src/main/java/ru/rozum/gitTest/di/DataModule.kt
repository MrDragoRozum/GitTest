package ru.rozum.gitTest.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.rozum.gitTest.R
import ru.rozum.gitTest.data.repository.AppRepositoryImpl
import ru.rozum.gitTest.domain.repository.AppRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface DataModule {

    @Binds
    @Singleton
    fun bindAppRepository(impl: AppRepositoryImpl): AppRepository

    companion object {
        @Provides
        fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
            context.getSharedPreferences(
                context.getString(R.string.token_gitHub),
                Context.MODE_PRIVATE
            )
    }
}