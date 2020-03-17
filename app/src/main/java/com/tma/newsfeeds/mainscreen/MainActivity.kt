package com.tma.newsfeeds.mainscreen

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tma.data.logmanager.LogDebug
import com.tma.data.model.Article
import com.tma.domain.mainscreen.MainPresenter
import com.tma.domain.mainscreen.MainView
import com.tma.newsfeeds.R
import com.tma.newsfeeds.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.exitProcess


class MainActivity : BaseActivity<MainPresenter>(), MainView, TextWatcher {
    private val TAG = "NewsFeeds"

    private var handler = Handler()

    override fun getLayout(): Int = R.layout.activity_main

    override fun initViews() {
        onClickClearEditTextSearch()
        initScrollListenerRecyclerView()
        callGetArticlesListApi()
    }

    /**
     * The method is called when the first time open app and load more of RecyclerView.
     */
    private fun callGetArticlesListApi() {
        if (presenter.getArticlesListFiltered().isEmpty()) {
            presenter.callGetArticlesListApi(true)
        } else {
            //rotate screen
            recyclerView.adapter = ArticlesListAdapter(presenter.getArticlesListFiltered())
        }
    }

    //===============================TextWatcher========================================
    override fun afterTextChanged(char: Editable?) {
        handleTextChange(char?.toString())
    }

    override fun beforeTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(char: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    /**
     * The method is called when user input text on EditText or click on X icon.
     */
    private fun handleTextChange(text: String?) {
        LogDebug.e(TAG, "handleTextChange() - $text")
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed({
            presenter.filterArticlesList(text)
        }, 500)

        showHideCloseSearchIcon(text == null || text.isEmpty())
    }

    private fun showHideCloseSearchIcon(isEmptyTextSearch: Boolean) {
        icCloseSearch.visibility = if (isEmptyTextSearch) View.INVISIBLE else View.VISIBLE
    }

    /**
     * Handle clicking on X icon to clear EditText and refresh articles list.
     */
    private fun onClickClearEditTextSearch() {
        icCloseSearch.setOnClickListener {
            if (edtSearch.text.isNotEmpty()) {
                edtSearch.text.clear()
                handleTextChange(null)
            }
        }
    }

    /**
     * The method will be called by onTextChanged of editSearch in MainActivity.
     * Or when GetArticlesListApi success.
     */
    override fun onFilterListSuccess(articlesListFiltered: ArrayList<Article>) {
        if (articlesListFiltered.isEmpty()) {
            Toast.makeText(this@MainActivity, getString(R.string.no_results), Toast.LENGTH_SHORT)
                .show()
        }
        LogDebug.e(TAG, "onFilterListSuccess, size = " + articlesListFiltered.size)
        if (recyclerView.adapter == null) {
            recyclerView.adapter = ArticlesListAdapter(articlesListFiltered)
        } else {
            (recyclerView.adapter as ArticlesListAdapter).updateArticlesList(articlesListFiltered)
        }
    }

    override fun getTextSearch(): String = edtSearch.text.toString()


    //===============================RecyclerView Load More=================================

    private fun initScrollListenerRecyclerView() {
        val linearLayout = recyclerView.layoutManager as LinearLayoutManager
        recyclerView.clearOnScrollListeners()
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var isLoading = false
            var totalItemCount = 0
            var lastVisibleItemPosition = 0

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dx == 0 && dy == 0) return
                if (!presenter.isCallingApi && !presenter.isLoadingFinalPage && presenter.connectivityReceiver.isNetworkConnected) {

                    totalItemCount = linearLayout.itemCount
                    lastVisibleItemPosition = (linearLayout.findLastVisibleItemPosition() + 3)
                    LogDebug.e(TAG, "count=$totalItemCount; lastVisible=$lastVisibleItemPosition")

                    if (!isLoading && totalItemCount != 0 && totalItemCount <= lastVisibleItemPosition) {
                        isLoading = true
                        addBottomProgressBar {
                            if (!presenter.isCallingApi && !presenter.isLoadingFinalPage && presenter.connectivityReceiver.isNetworkConnected) {
                                presenter.callGetArticlesListApi(false)
                            }
                            isLoading = false
                        }

                    }
                }
            }
        })
    }

    override fun addBottomProgressBar(callback: () -> Unit) {
        //we searched and found 2 items on list, then we change screen to landscape
        //An exception happened: Cannot call this method in a scroll callback.
        //Scroll callbacks mightbe run during a measure & layout pass where you cannot change theRecyclerView data.
        //-> Use recyclerView.post to fix this issue.
        recyclerView.post {
            (recyclerView.adapter as? ArticlesListAdapter)?.run {
                addBottomProgressBar()
                callback.invoke()
            }
        }
    }

    override fun removeBottomProgressBar(isNotifyItemRemoved: Boolean) {
        (recyclerView.adapter as? ArticlesListAdapter)?.run {
            removeBottomProgressBar(isNotifyItemRemoved)
        }
    }

    //===============================ConnectivityReceiver=================================
    override fun onStart() {
        super.onStart()
        presenter.connectivityReceiver.registerReceiver(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.connectivityReceiver.connectivityReceiverListener = presenter
        /*
            Listen text change to filter articles list.
            Must not call this code in onCreate() (ex: initViews())
            to avoid afterTextChanged() will be called whenever rotate screen
         */
        showHideCloseSearchIcon(edtSearch.text.isEmpty())
        edtSearch.addTextChangedListener(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.connectivityReceiver.unregisterReceiver(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        exitProcess(0)
    }

    override fun showToastError() {
        Toast.makeText(applicationContext, R.string.an_error_occurred, Toast.LENGTH_SHORT)
            .show()
    }

    override fun showToastPleaseCheckNetwork() {
        Toast.makeText(applicationContext, R.string.please_check_internet, Toast.LENGTH_SHORT)
            .show()
    }
}
