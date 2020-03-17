package com.tma.newsfeeds.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tma.domain.base.BasePresenter
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseFragment<P : BasePresenter<Any>> : DaggerFragment() {

    @Inject
    lateinit var presenter: P

    protected abstract fun getLayout(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(getLayout(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPresenter()
    }

    private fun initPresenter() {
        presenter.attachView(this)
        presenter.initialise()
        initViews()
    }

    override fun onDestroy() {
        presenter.disposeSubscriptions()
        presenter.detachView()
        super.onDestroy()
    }

    abstract fun initViews()

    fun showProgressDialog(text: String? = null) {
        val activity = activity as? BaseActivity<*> ?: return
        activity.showProgressDialog(text)
    }

    fun hideProgressDialog() {
        val activity = activity as? BaseActivity<*> ?: return
        activity.hideProgressDialog()
    }
}