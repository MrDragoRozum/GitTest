package ru.rozum.gitTest.di.markwon

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.noties.markwon.Markwon
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import io.noties.markwon.syntax.Prism4jThemeDarkula
import io.noties.markwon.syntax.SyntaxHighlightPlugin
import io.noties.prism4j.Prism4j
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object MarkwonModule {

    @Provides
    @Singleton
    fun provideMarkdown(@ApplicationContext context: Context): Markwon {
        val prism4j = Prism4j(GrammarLocatorDef())
        val prism4jTheme = Prism4jThemeDarkula.create()

        return Markwon.builder(context)
            .usePlugin(TaskListPlugin.create(context))
            .usePlugin(LinkifyPlugin.create())
            .usePlugin(TablePlugin.create(context))
            .usePlugin(ImagesPlugin.create())
            .usePlugin(SyntaxHighlightPlugin.create(prism4j, prism4jTheme))
            .build()
    }
}