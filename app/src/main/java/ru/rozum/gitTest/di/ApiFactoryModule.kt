package ru.rozum.gitTest.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.rozum.gitTest.data.local.KeyValueStorage
import ru.rozum.gitTest.data.network.api.ApiGitHubService
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ApiFactoryModule {

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiGitHubService =
        retrofit.create(ApiGitHubService::class.java)

    @Provides
    @Singleton
    fun provideRetrofit(keyValueStorage: KeyValueStorage): Retrofit {
        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }
        val logging = HttpLoggingInterceptor().apply {
            level = Level.BODY
        }
        val token = keyValueStorage.getToken()
        val client = getOkHttpClient(token, logging)

        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(json.asConverterFactory(CONTENT_TYPE.toMediaType()))
            .client(client)
            .baseUrl(URL_API_GITHUB)
            .build()
    }

    private fun getOkHttpClient(token: String, logging: HttpLoggingInterceptor) =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()

                if (isRequestSignInGitHub(originalRequest) || isRequestGetReadme(originalRequest))
                    return@addInterceptor chain.proceed(originalRequest)

                val newHeaders = originalRequest.headers.newBuilder()
                    .add(HEADER_ACCEPT, HEADER_ACCEPT_BODY)
                    .add(HEADER_TOKEN, "$HEADER_TOKEN_BODY $token")
                    .build()

                val newRequest = originalRequest
                    .newBuilder()
                    .headers(newHeaders)
                    .build()

                chain.proceed(newRequest)
            }
            .addInterceptor(logging)
            .build()


    private fun isRequestGetReadme(request: Request) =
        request.url.toString().contains(URL_RAW_GITHUB)

    private fun isRequestSignInGitHub(request: Request) =
        request.url.toString() == URL_API_GITHUB + "user"

    private const val URL_API_GITHUB = "https://api.github.com/"
    private const val URL_RAW_GITHUB = "https://raw.githubusercontent.com/"

    private const val CONTENT_TYPE = "application/vnd.github+json"

    private const val HEADER_ACCEPT = "Accept"
    private const val HEADER_ACCEPT_BODY =
        "application/vnd.github+json X-GitHub-Api-Version:2022-11-28"

    private const val HEADER_TOKEN = "Authorization"
    private const val HEADER_TOKEN_BODY = "bearer "
}



