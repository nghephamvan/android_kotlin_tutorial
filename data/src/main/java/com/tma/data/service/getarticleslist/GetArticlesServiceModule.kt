package com.tma.data.service.getarticleslist

import com.tma.data.retrofit.module.RetrofitModule
import com.tma.data.scope.ActivityScope
import com.tma.data.service.SchedulerModule
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import retrofit2.Retrofit
import javax.inject.Named

@Module(includes = [SchedulerModule::class])
class GetArticlesServiceModule {
    @ActivityScope
    @Provides
    fun provideGetArticlesApi(@Named(RetrofitModule.RX_RETROFIT) retrofit: Retrofit): GetArticlesApi =
        retrofit.create(GetArticlesApi::class.java)

    @ActivityScope
    @Provides
    internal fun provideGetArticlesService(
        getArticlesApi: GetArticlesApi,
        @Named(SchedulerModule.RX_IO_SCHEDULER) ioScheduler: Scheduler,
        @Named(SchedulerModule.RX_MAIN_THREAD_SCHEDULER) mainThreadScheduler: Scheduler
    ): GetArticlesService = GetArticlesService(getArticlesApi, ioScheduler, mainThreadScheduler)
}