package com.tma.domain.mainscreen

import com.tma.data.logmanager.LogDebug
import com.tma.data.model.NewsList
import io.reactivex.observers.DisposableSingleObserver

class MainObserver(val presenter: MainPresenter) : DisposableSingleObserver<NewsList>() {
    override fun onSuccess(t: NewsList) {
        presenter.onSuccessGetArticlesListApi(t)
    }

    override fun onError(e: Throwable) {
        LogDebug.e("MainObserver", "MainObserver -> onError", e)
        presenter.onError()
    }
}