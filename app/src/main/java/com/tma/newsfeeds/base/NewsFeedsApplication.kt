package com.tma.newsfeeds.base

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class NewsFeedsApplication : DaggerApplication() {

    private val applicationComponent: AndroidInjector<NewsFeedsApplication> by lazy {
        DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        applicationComponent.inject(this)
        //RealmDatabase.initializeRealm(this)
        //SharedPref.initSharedPreferences(sharedPreferences)
        //Fabric.with(this, Crashlytics())
    }

    override fun applicationInjector(): AndroidInjector<NewsFeedsApplication>? {
        return applicationComponent
    }
}