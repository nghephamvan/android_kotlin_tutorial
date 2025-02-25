package com.tma.data.service

import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseService<T, in Params>(
    private val subscribeScheduler: Scheduler,
    private val postExecutionScheduler: Scheduler
) {

    private val disposables: CompositeDisposable = CompositeDisposable()

    abstract fun buildServiceSingle(params: Params?): Single<T>

    fun execute(observer: SingleObserver<T>, params: Params? = null) {
        val observable: Single<T> = this.buildServiceSingle(params)
            .subscribeOn(subscribeScheduler)
            .observeOn(postExecutionScheduler)
        (observable.subscribeWith(observer) as? Disposable)?.let {
            disposables.add(it)
        }
    }

    fun dispose() {
        disposables.clear()
    }
}