package vn.aki.di.module

import android.content.Context
import dagger.Module
import dagger.Provides
import vn.aki.AkiApplication
import javax.inject.Singleton

@Module
class AppModule {
    @Provides
    @Singleton
    fun provideApplication(application: AkiApplication): Context = application

    @Provides
    @Singleton
    fun provideString(application: AkiApplication): String = "Good morning"
}