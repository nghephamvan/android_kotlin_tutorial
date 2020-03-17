package com.tma.domain.base

interface BaseView {
    fun lockOrientation()
    fun unlockOrientation()
    fun showProgressDialog(text: String? = null)
    fun hideProgressDialog()
}