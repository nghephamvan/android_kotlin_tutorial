package com.tma.newsfeeds.base

import android.app.Application
import com.tma.data.scope.ActivityScope
import com.tma.domain.detailscreen.DetailModule
import com.tma.domain.mainscreen.MainModule
import com.tma.newsfeeds.detailscreen.DetailActivity
import com.tma.newsfeeds.mainscreen.MainActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Singleton

@Module
abstract class AppModule {
    @Binds
    @Singleton
    abstract fun application(app: NewsFeedsApplication): Application

    @ActivityScope
    @ContributesAndroidInjector(modules = [MainModule::class])
    internal abstract fun mainActivity(): MainActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [DetailModule::class])
    internal abstract fun detailActivity(): DetailActivity
}