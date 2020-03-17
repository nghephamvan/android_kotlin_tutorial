package com.tma.newsfeeds.base

import com.tma.data.retrofit.module.RetrofitModule
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class, AppModule::class, RetrofitModule::class]
)
interface AppComponent : AndroidInjector<NewsFeedsApplication> {
    @Component.Factory
    abstract class Factorys : AndroidInjector.Factory<NewsFeedsApplication>
}