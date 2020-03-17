package com.tma.domain.mainscreen

import com.tma.data.retrofit.ConnectivityReceiver
import com.tma.data.scope.ActivityScope
import com.tma.data.service.getarticleslist.GetArticlesService
import com.tma.data.service.getarticleslist.GetArticlesServiceModule
import dagger.Module
import dagger.Provides

@Module(includes = [GetArticlesServiceModule::class])
class MainModule {
    @ActivityScope
    @Provides
    internal fun provideMainPresenter(
        getArticlesService: GetArticlesService,
        connectivityReceiver: ConnectivityReceiver
    ) = MainPresenter(getArticlesService, connectivityReceiver)
}