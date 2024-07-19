package ru.rozum.gitTest.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RegexModule {

    /*
    *  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 — Success
    *  ghp_Spw90v7Sumuwq8JxuiBsGgq12U1Pga26vВКЛ — Invalid Token
    *  ghp_Spw90v7Sumuwq8JxuiBsGgq12U1Pga26v — Invalid Token
    */
    @Provides
    @RegexLegalTokenQualifier
    fun provideRegexLegalToken(): Regex = Regex("^bearer ghp_[a-zA-Z0-9]{36}+\$")
}