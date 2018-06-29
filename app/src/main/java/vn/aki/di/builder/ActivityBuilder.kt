package vn.aki.di.builder

import dagger.Module
import dagger.android.ContributesAndroidInjector
import vn.aki.activities.MainActivity

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract fun bindMainActivity(): MainActivity
}