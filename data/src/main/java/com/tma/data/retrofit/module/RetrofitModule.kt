package com.tma.data.retrofit.module

import android.app.Application
import android.content.Context
import com.google.gson.GsonBuilder
import com.tma.data.BuildConfig
import com.tma.data.retrofit.CommonInterceptor
import com.tma.data.retrofit.ConnectivityReceiver
import com.tma.data.retrofit.MemoryCookieStore
import com.tma.data.retrofit.NetworkMonitor
import dagger.Module
import dagger.Provides
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class RetrofitModule {
    companion object {
        const val RX_APP_CONTEXT = "rx_app"
        const val RX_RETROFIT = "rx_retrofit"

        const val CONNECTION_TIMEOUT = 60L
        const val KEEP_ALIVE_DURATION = 60L
        const val MAX_IDLE_CONNECTIONS = 5

        const val BASE_URL = "https://api.myjson.com/"
    }

    @Provides
    @Named(RX_APP_CONTEXT)
    @Singleton
    fun provideAppContext(app: Application): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideNetworkMonitor(@Named(RX_APP_CONTEXT) context: Context): NetworkMonitor = NetworkMonitor(context)

    @Provides
    @Singleton
    fun provideConnectivityReceiver(networkMonitor: NetworkMonitor): ConnectivityReceiver = ConnectivityReceiver(networkMonitor)

    @Provides
    @Singleton
    fun provideCommonInterceptor(networkMonitor: NetworkMonitor): CommonInterceptor = CommonInterceptor(networkMonitor)

    @Provides
    @Singleton
    fun provideMemoryCookieStore() = MemoryCookieStore()

    @Provides
    @Singleton
    fun provideOKHttpClient(commonInterceptor: CommonInterceptor, memoryCookieStore: MemoryCookieStore): OkHttpClient =
        OkHttpClient.Builder().apply {
            cookieJar(memoryCookieStore)
            connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
            connectionPool(ConnectionPool(MAX_IDLE_CONNECTIONS, KEEP_ALIVE_DURATION, TimeUnit.SECONDS))
            addInterceptor(commonInterceptor)
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            } else {
                addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.NONE })
            }
        }.build()


    @Provides
    @Singleton
    @Named(RX_RETROFIT)
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder().apply {
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            client(okHttpClient)
            baseUrl(BASE_URL)
        }.build()
}