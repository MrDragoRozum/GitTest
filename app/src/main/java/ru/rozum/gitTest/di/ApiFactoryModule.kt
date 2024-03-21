package ru.rozum.gitTest.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.rozum.gitTest.data.network.api.*

@InstallIn(SingletonComponent::class)
@Module
object ApiFactoryModule {

    @Provides
    @ApplicationScope
    fun provideApiService(@ApiQualifier retrofit: Retrofit): ApiGitHubService =
        retrofit.create(ApiGitHubService::class.java)

    @Provides
    @ApplicationScope
    fun provideRawService(@RawQualifier retrofit: Retrofit): RawGitHubService =
        retrofit.create(RawGitHubService::class.java)

    @RawQualifier
    @Provides
    @ApplicationScope
    fun provideRetrofitRaw(): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(URL_RAW_GITHUB)
            .build()

    @ApiQualifier
    @Provides
    @ApplicationScope
    fun provideRetrofitApi(): Retrofit {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .addConverterFactory(json.asConverterFactory(CONTENT_TYPE.toMediaType()))
            .baseUrl(URL_API_GITHUB)
            .build()
    }

    private const val URL_API_GITHUB = "https://api.github.com/"
    private const val URL_RAW_GITHUB = "https://raw.githubusercontent.com/"
    private const val CONTENT_TYPE = "application/vnd.github+json"
}



