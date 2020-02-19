package vn.aki

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import vn.aki.di.component.DaggerAppComponent
import javax.inject.Inject

class AkiApplication: Application(), HasActivityInjector {
    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingActivityInjector

    override fun onCreate() {
        super.onCreate()

        DaggerAppComponent
                .builder()
                .application(this)
                .build()
                .inject(this)

    }
}