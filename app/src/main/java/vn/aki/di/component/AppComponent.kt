package vn.aki.di.component

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.support.AndroidSupportInjectionModule
import vn.aki.AkiApplication
import vn.aki.di.builder.ActivityBuilder
import vn.aki.di.module.AppModule
import javax.inject.Singleton

@Singleton
@Component(modules=arrayOf(AppModule::class, AndroidInjectionModule::class, ActivityBuilder::class))
interface AppComponent {

    @Component.Builder
    interface Builder
    {
        @BindsInstance
        fun application(application: AkiApplication): Builder

        fun build(): AppComponent
    }

    fun inject (app: AkiApplication)
}