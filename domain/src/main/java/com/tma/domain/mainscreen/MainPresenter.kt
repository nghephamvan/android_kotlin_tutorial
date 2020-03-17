package com.tma.domain.mainscreen

import com.tma.data.logmanager.LogDebug
import com.tma.data.model.Article
import com.tma.data.model.NewsList
import com.tma.data.retrofit.ConnectivityReceiver
import com.tma.data.retrofit.ConnectivityReceiverListener
import com.tma.data.service.getarticleslist.GetArticlesService
import com.tma.domain.base.BasePresenter
import com.tma.domain.base.BaseView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class MainPresenter @Inject constructor(
    private val getArticlesService: GetArticlesService,
    val connectivityReceiver: ConnectivityReceiver
) : BasePresenter<MainView>(), ConnectivityReceiverListener {
    private val TAG = "NewsFeeds"
    /**
     * Use to dispose FilterObserver when activity is destroyed
     */
    private var disposable: Disposable? = null
    /**
     * true -> when calling api
     * false -> when onSuccessGetArticlesListApi() -> onFilterSuccess()
     */
    var isCallingApi = false
    /**
     * When recyclerView is scrolled to last item, if this variable is
     * false -> call api to get next page data
     * true -> don't call api
     */
    var isLoadingFinalPage = false

    /**
     * The method is called when internet is on/off
     * If is also called when onStart() in MainActivity register network receiver.
     */
    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            if (staticArticlesListFiltered.isEmpty() && !isCallingApi && !isLoadingFinalPage) {
                callGetArticlesListApi(true)
            }
        }
    }

    override fun initialise() {
        if (staticCurrentPage == 5) {
            isLoadingFinalPage = true
        }
    }

    override fun disposeSubscriptions() {
        getArticlesService.dispose()
        disposable?.dispose()
        //When Bottom Progress Bar is displaying, user open Detail screen and rotate screen
        //-> We must remove Bottom Progress Bar for Main screen and unlock orientation.
        getView()?.run {
            removeBottomProgressBar(false)
            unlockOrientation()
        }
    }

    fun callGetArticlesListApi(isShowProgressDialog: Boolean) {
        if (isCallingApi || isLoadingFinalPage) return

        isCallingApi = true

        getView()?.run {
            lockOrientation()
            if (isShowProgressDialog) {
                showProgressDialog()
            }
        }
        if (connectivityReceiver.isNetworkConnected) {
            var apiPageId: String? = null
            //TODO HardCode 5 pages
            when (staticCurrentPage) {
                0 -> apiPageId = GetArticlesService.API_PAGE_1
                1 -> apiPageId = GetArticlesService.API_PAGE_2
                2 -> apiPageId = GetArticlesService.API_PAGE_3
                3 -> apiPageId = GetArticlesService.API_PAGE_4
                4 -> apiPageId = GetArticlesService.API_PAGE_5
                else -> {
                }
            }
            if (apiPageId != null) {
                getArticlesService.dispose()
                getArticlesService.execute(provideMainObserver(), apiPageId)
            } else {
                LogDebug.e(TAG, "callGetArticlesListApi -> Error Current Page")
                getView()?.run {
                    hideProgressDialog()
                    showToastError()
                    removeBottomProgressBar()
                    unlockOrientation()
                    //current page is not 0, 1, 2, 3 or 4 -> isCallingApi = true
                    //-> api returned an error page: out of scope -> Stop calling api.
                }

            }
        } else {
            getView()?.run {
                hideProgressDialog()
                showToastPleaseCheckNetwork()
                removeBottomProgressBar()
                unlockOrientation()
                isCallingApi = false
            }
        }
    }

    internal fun provideMainObserver() = MainObserver(this)

    /**
     * The method is called when api success.
     */
    internal fun onSuccessGetArticlesListApi(newsList: NewsList) {
        getView()?.run {
            staticCurrentPage = newsList.currentPage
            if (staticCurrentPage == 5) {
                //TODO HardCode only have 5 pages
                isLoadingFinalPage = true
            }
            //only remove progress bar, not notify item removed because notifyDataSetChanged()
            //will be called after filter success
            removeBottomProgressBar(false)
            staticArticlesList.addAll(newsList.articles)
            //
            filterArticlesList(getTextSearch())
        }
    }

    /**
     * The method is called when api fail.
     */
    internal fun onError() {
        LogDebug.e(TAG, "MainPresenter -> onError")
        getView()?.run {
            hideProgressDialog()
            showToastError()
            removeBottomProgressBar()
            unlockOrientation()
        }
        isCallingApi = false
    }

    /**
     * The method will be called by afterTextChanged of editSearch in MainActivity.
     * Or when GetArticlesListApi success.
     */
    fun filterArticlesList(text: String?) {
        if (text == null || text.isEmpty()) {
            onFilterSuccess(staticArticlesList)
        } else {
            val locale = Locale.getDefault()
            val textLower = text.toLowerCase(locale)
            disposable?.dispose()
            disposable = (Observable.fromIterable(staticArticlesList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter { article -> article.title.toLowerCase(locale).contains(textLower) }
                .toList()
                .subscribeWith(FilterObserver(this@MainPresenter)))
        }
    }

    /**
     * The method is called when filter success.
     */
    internal fun onFilterSuccess(result: ArrayList<Article>) {
        LogDebug.d(TAG, "onFilterSuccess")
        /*
            Instead of checking result.isEmpty(), we must check result.size < 4.
            Devices xxxhdpi displays about 3.5 item on screen
            Ex: we search any text and we find only 2 items on local list -> RecyclerView will display
            2 that items on screen and then we cannot scrolled RecyclerView -> we cannot load more
            => if we check result.size < 4, this code will continue to call next api until size >= 4
            RecyclerView will enable scroll event.
         */
        if (result.size < 4 && !isLoadingFinalPage) {
            /*
                when user input text search which don't exist in local articles list
                the method onScrolled of RecyclerView in MainActivity will trigger to call api
                to get new data. If still not find any article object match to filter condition,
                the app will continue to call next api until final api (final page).
            */
            getView()?.run {
                isCallingApi = false
                callGetArticlesListApi(true)
                return@onFilterSuccess
            }
        }
        staticArticlesListFiltered = result
        getView()?.run {
            onFilterListSuccess(staticArticlesListFiltered)
            hideProgressDialog()
            unlockOrientation()
        }
        isCallingApi = false
    }

    fun getArticlesListFiltered(): ArrayList<Article> = staticArticlesListFiltered

    companion object {
        var staticCurrentPage: Int = 0
        var staticArticlesList: ArrayList<Article> = ArrayList()
        var staticArticlesListFiltered: ArrayList<Article> = ArrayList()
    }
}

interface MainView : BaseView {
    fun getTextSearch(): String
    fun onFilterListSuccess(articlesListFiltered: ArrayList<Article>)
    fun addBottomProgressBar(callback: () -> Unit)
    fun removeBottomProgressBar(isNotifyItemRemoved: Boolean = true)
    fun showToastError()
    fun showToastPleaseCheckNetwork()
}