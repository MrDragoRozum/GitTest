package ru.rozum.gitTest.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.skydoves.sandwich.retrofit.adapters.ApiResponseCallAdapterFactory
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
        val token = keyValueStorage.token
        val client = getOkHttpClient(token, logging)

        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addCallAdapterFactory(ApiResponseCallAdapterFactory.create())
            .addConverterFactory(json.asConverterFactory(CONTENT_TYPE.toMediaType()))
            .client(client)
            .baseUrl(URL_API_GITHUB)
            .build()
    }

    private fun getOkHttpClient(token: String, logging: HttpLoggingInterceptor) =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()

                if (isRequestGetReadme(originalRequest))
                    return@addInterceptor chain.proceed(originalRequest)

                if (isRequestSignInGitHub(originalRequest)) {
                    val currentHeader = originalRequest.headers[HEADER_TOKEN]
                        ?: error("Header \"Authorization\" не найден!")

                    val newHeaderBody = "bearer $currentHeader"
                    val newHeaders = originalRequest.headers.newBuilder().set(
                        HEADER_TOKEN, newHeaderBody
                    ).build()

                    val newRequest = originalRequest.newBuilder()
                        .headers(newHeaders)
                        .build()

                    return@addInterceptor chain.proceed(newRequest)
                }

                val newRequest = getNewRequestWithAddedHeaders(originalRequest, token)
                chain.proceed(newRequest)
            }
            .addInterceptor(logging)
            .build()

    private fun getNewRequestWithAddedHeaders(originalRequest: Request, token: String): Request {
        val newHeaders = originalRequest.headers.newBuilder()
            .add(HEADER_ACCEPT, HEADER_ACCEPT_BODY)
            .add(HEADER_TOKEN, "$HEADER_TOKEN_BODY $token")
            .build()

        val newRequest = originalRequest
            .newBuilder()
            .headers(newHeaders)
            .build()
        return newRequest
    }


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



