package ru.rozum.gitTest.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.rozum.gitTest.data.repository.GithubRepoRepositoryImpl
import ru.rozum.gitTest.data.repository.UserRepositoryImpl
import ru.rozum.gitTest.domain.repository.GithubRepoRepository
import ru.rozum.gitTest.domain.repository.UserRepository
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface DataModule {

    @Binds
    @Singleton
    fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    fun bindGitHubRepoRepository(impl: GithubRepoRepositoryImpl): GithubRepoRepository

    companion object {

        private const val SECRET_TOKEN_GITHUB = "secret_token"

        @Provides
        fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPreferences = EncryptedSharedPreferences(
                context = context,
                fileName = SECRET_TOKEN_GITHUB,
                masterKey = masterKey,
                prefKeyEncryptionScheme = EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                prefValueEncryptionScheme = EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )

            return sharedPreferences
        }
    }
}