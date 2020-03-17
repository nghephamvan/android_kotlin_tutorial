package com.tma.domain.detailscreen

import com.tma.data.scope.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class DetailModule {
    @ActivityScope
    @Provides
    internal fun provideMainPresenter() = DetailPresenter()
}
