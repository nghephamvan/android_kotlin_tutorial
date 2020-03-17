package com.tma.newsfeeds.base

import android.app.Dialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.tma.domain.base.BasePresenter
import com.tma.domain.base.BaseView
import com.tma.newsfeeds.R
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.custom_progress_dialog.*
import javax.inject.Inject

abstract class BaseActivity<P : BasePresenter<Any>> : DaggerAppCompatActivity(),
    BaseView {

    @Inject
    lateinit var presenter: P

    protected abstract fun getLayout(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.run {
            setCustomView(R.layout.action_bar)
            setDisplayHomeAsUpEnabled(false)
            setDisplayShowTitleEnabled(false)
            setDisplayShowCustomEnabled(true)
        }


        setContentView(getLayout())
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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

    override fun lockOrientation() {
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
    }

    override fun unlockOrientation() {
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
    }

    fun setPendingTransitionInRightOutLeft(inRightOutLeft: Boolean) {
        if (inRightOutLeft) {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        } else {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    fun setPendingTransitionInBottomOutNothing(inBottom: Boolean) {
        if (inBottom) {
            overridePendingTransition(R.anim.slide_in_bottom, R.anim.nothing)
        } else {
            overridePendingTransition(R.anim.nothing, R.anim.slide_out_bottom)
        }
    }

    fun setPendingTransitionNothing() {
        overridePendingTransition(R.anim.nothing, R.anim.nothing)
    }

    private var nowShowing: Boolean = false
    private var progressDialog: Dialog? = null

    override fun showProgressDialog(text: String?) {
        if (!nowShowing) {
            if (progressDialog == null) {
                progressDialog = Dialog(this).apply {
                    requestWindowFeature(Window.FEATURE_NO_TITLE) //before
                    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    setCancelable(false)
                    setCanceledOnTouchOutside(false)
                    setContentView(R.layout.custom_progress_dialog)
                    if (!text.isNullOrEmpty()) {
                        txt_dialog.text = text
                    }
                    show()
                    nowShowing = true
                }
            }

        }
    }

    override fun hideProgressDialog() {
        if (nowShowing) {
            progressDialog?.dismiss()
            progressDialog = null
            nowShowing = false
        }
    }

    fun restartApp() {
        packageManager.getLaunchIntentForPackage(packageName).apply {
            if (this != null) {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                this@BaseActivity.finishAffinity()
            }
        }
    }
}