package com.tma.domain.mainscreen

import com.tma.data.logmanager.LogDebug
import com.tma.data.model.Article
import io.reactivex.observers.DisposableSingleObserver

class FilterObserver(private val presenter: MainPresenter) :
    DisposableSingleObserver<List<Article>>() {
    override fun onSuccess(t: List<Article>) {
        presenter.onFilterSuccess(t as ArrayList<Article>)
    }

    override fun onError(e: Throwable) {
        LogDebug.e("FilterObserver", "FilterObserver -> onError", e)
        presenter.getView()?.run {
            hideProgressDialog()
            showToastError()
            removeBottomProgressBar()
            unlockOrientation()
        }
        presenter.isCallingApi = false
    }
}