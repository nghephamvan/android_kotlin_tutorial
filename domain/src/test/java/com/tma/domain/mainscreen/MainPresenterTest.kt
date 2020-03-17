package com.tma.domain.mainscreen

import com.tma.data.model.Article
import com.tma.data.model.NewsList
import com.tma.data.retrofit.ConnectivityReceiver
import com.tma.data.retrofit.ConnectivityReceiverListener
import com.tma.data.service.getarticleslist.GetArticlesService
import io.reactivex.disposables.Disposable
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class MainPresenterTest {
    private fun <T> any(type: Class<T>): T = Mockito.any<T>(type)

    @Mock
    lateinit var mockGetArticlesService: GetArticlesService
    @Mock
    lateinit var mockConnectivityReceiver: ConnectivityReceiver
    @Mock
    lateinit var mockMainView: MainView
    @Mock
    lateinit var mockConnectivityReceiverListener: ConnectivityReceiverListener

    private lateinit var mainPresenter: MainPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        mainPresenter = spy(MainPresenter(mockGetArticlesService, mockConnectivityReceiver))
        mainPresenter.attachView(mockMainView)
        mainPresenter.connectivityReceiver.connectivityReceiverListener =
            mockConnectivityReceiverListener
    }

    @Test
    fun test_onNetworkConnectionChangedFalse() {
        //run
        mainPresenter.onNetworkConnectionChanged(false)
        //verify
        verifyNoMoreInteractions(mockMainView)
    }

    @Test
    fun test_onNetworkConnectionChangedTrue_articlesListNotEmpty() {
        //prepare mock data
        MainPresenter.staticArticlesListFiltered = ArrayList()
        MainPresenter.staticArticlesListFiltered.add(Article())
        //run
        mainPresenter.onNetworkConnectionChanged(true)
        //verify
        verifyNoMoreInteractions(mockMainView)
    }

    @Test
    fun test_onNetworkConnectionChangedTrue_articlesListEmpty_isCallingApiTrue() {
        //prepare mock data
        MainPresenter.staticArticlesListFiltered = ArrayList()
        mainPresenter.isCallingApi = true
        //run
        mainPresenter.onNetworkConnectionChanged(true)
        //verify
        verifyNoMoreInteractions(mockMainView)
    }

    @Test
    fun test_onNetworkConnectionChangedTrue_articlesListEmpty_isCallingApiFalse_isLoadedFinalPageTrue() {
        //prepare mock data
        MainPresenter.staticArticlesListFiltered = ArrayList()
        mainPresenter.isCallingApi = false
        mainPresenter.isLoadingFinalPage = true
        //run
        mainPresenter.onNetworkConnectionChanged(true)
        //verify
        verifyNoMoreInteractions(mockMainView)
    }

    @Test
    fun test_onNetworkConnectionChangedTrue_articlesListEmpty_isCallingApiFalse_isLoadedFinalPageFalse() {
        //prepare mock data
        MainPresenter.staticArticlesListFiltered = ArrayList()
        mainPresenter.isCallingApi = false
        mainPresenter.isLoadingFinalPage = false
        //run
        mainPresenter.onNetworkConnectionChanged(true)
        //verify
        verify(mainPresenter, times(1)).callGetArticlesListApi(true)
    }

    @Test
    fun test_initialise_staticCurrentPageIs5() {
        //prepare mock data
        mainPresenter.isLoadingFinalPage = false
        MainPresenter.staticCurrentPage = 5
        //run
        mainPresenter.initialise()
        //verify
        Assert.assertTrue(mainPresenter.isLoadingFinalPage)
    }

    @Test
    fun test_initialise_staticCurrentPageIsNot5() {
        //prepare mock data
        mainPresenter.isLoadingFinalPage = false
        MainPresenter.staticCurrentPage = 0
        //run
        mainPresenter.initialise()
        //verify
        Assert.assertFalse(mainPresenter.isLoadingFinalPage)
    }

    @Test
    fun test_disposeSubscriptions() {
        //prepare mock data
        //access private variable and mock data for it
        val disposableMock: Disposable = mock(Disposable::class.java)
        val privateDisposable = spy(mainPresenter.javaClass.getDeclaredField("disposable").apply {
            isAccessible = true
        })
        privateDisposable.set(mainPresenter, disposableMock)
        //run
        mainPresenter.disposeSubscriptions()
        //verify
        verify(mockGetArticlesService, times(1)).dispose()
        verify(disposableMock, times(1)).dispose()
        verify(mockMainView, times(1)).removeBottomProgressBar(false)
        verify(mockMainView, times(1)).unlockOrientation()
        verifyNoMoreInteractions(mockMainView)
    }

    @Test
    fun test_callGetArticlesListApi_isCallingApiTrue() {
        //prepare mock data
        mainPresenter.isCallingApi = true
        mainPresenter.isLoadingFinalPage = false
        //run
        mainPresenter.callGetArticlesListApi(true)
        //verify
        Assert.assertTrue(mainPresenter.isCallingApi)
        Assert.assertFalse(mainPresenter.isLoadingFinalPage)
        verifyNoInteractions(mockMainView)
    }

    @Test
    fun test_callGetArticlesListApi_isLoadingFinalPageTrue() {
        //prepare mock data
        mainPresenter.isCallingApi = false
        mainPresenter.isLoadingFinalPage = true
        //run
        mainPresenter.callGetArticlesListApi(true)
        //verify
        Assert.assertFalse(mainPresenter.isCallingApi)
        Assert.assertTrue(mainPresenter.isLoadingFinalPage)

        verifyNoInteractions(mockMainView)
    }

    @Test
    fun test_callGetArticlesListApi_isShowProgressDialogTrue_isNetworkConnectedFalse() {
        //prepare mock data
        mainPresenter.isCallingApi = false
        mainPresenter.isLoadingFinalPage = false
        val isShowProgressDialog = true
        //when
        `when`(mainPresenter.connectivityReceiver.isNetworkConnected).thenReturn(false)
        //run
        mainPresenter.callGetArticlesListApi(isShowProgressDialog)
        //verify
        val mockMainViewInOrder = inOrder(mockMainView)
        mockMainViewInOrder.verify(mockMainView, times(1)).lockOrientation()
        mockMainViewInOrder.verify(mockMainView, times(1)).showProgressDialog()
        mockMainViewInOrder.verify(mockMainView, times(1)).hideProgressDialog()
        mockMainViewInOrder.verify(mockMainView, times(1)).showToastPleaseCheckNetwork()
        mockMainViewInOrder.verify(mockMainView, times(1)).removeBottomProgressBar()
        mockMainViewInOrder.verify(mockMainView, times(1)).unlockOrientation()

        Assert.assertFalse(mainPresenter.isCallingApi)
        Assert.assertFalse(mainPresenter.isLoadingFinalPage)
    }

    @Test
    fun test_callGetArticlesListApi_isShowProgressDialogFalse_isNetworkConnectedTrue_staticCurrentPageEquals6() {
        //prepare mock data
        mainPresenter.isCallingApi = false
        mainPresenter.isLoadingFinalPage = false
        val isShowProgressDialog = false
        MainPresenter.staticCurrentPage = 6
        //when
        `when`(mainPresenter.connectivityReceiver.isNetworkConnected).thenReturn(true)
        //run
        mainPresenter.callGetArticlesListApi(isShowProgressDialog)
        //verify
        val mockMainViewInOrder = inOrder(mockMainView)
        mockMainViewInOrder.verify(mockMainView, times(1)).lockOrientation()
        mockMainViewInOrder.verify(mockMainView, never()).showProgressDialog()
        mockMainViewInOrder.verify(mockMainView, times(1)).hideProgressDialog()
        mockMainViewInOrder.verify(mockMainView, times(1)).showToastError()
        mockMainViewInOrder.verify(mockMainView, times(1)).removeBottomProgressBar()
        mockMainViewInOrder.verify(mockMainView, times(1)).unlockOrientation()

        Assert.assertTrue(mainPresenter.isCallingApi)
        Assert.assertFalse(mainPresenter.isLoadingFinalPage)
    }

    @Test
    fun test_callGetArticlesListApi_isShowProgressDialogFalse_isNetworkConnectedTrue_staticCurrentPageEquals0() {
        test_callGetArticlesListApi_isShowProgressDialogFalse_isNetworkConnectedTrue(0)
    }

    @Test
    fun test_callGetArticlesListApi_isShowProgressDialogFalse_isNetworkConnectedTrue_staticCurrentPageEquals1() {
        test_callGetArticlesListApi_isShowProgressDialogFalse_isNetworkConnectedTrue(1)
    }

    @Test
    fun test_callGetArticlesListApi_isShowProgressDialogFalse_isNetworkConnectedTrue_staticCurrentPageEquals2() {
        test_callGetArticlesListApi_isShowProgressDialogFalse_isNetworkConnectedTrue(2)
    }

    @Test
    fun test_callGetArticlesListApi_isShowProgressDialogFalse_isNetworkConnectedTrue_staticCurrentPageEquals3() {
        test_callGetArticlesListApi_isShowProgressDialogFalse_isNetworkConnectedTrue(3)
    }

    @Test
    fun test_callGetArticlesListApi_isShowProgressDialogFalse_isNetworkConnectedTrue_staticCurrentPageEquals4() {
        test_callGetArticlesListApi_isShowProgressDialogFalse_isNetworkConnectedTrue(4)
    }

    private fun test_callGetArticlesListApi_isShowProgressDialogFalse_isNetworkConnectedTrue(
        currentPage: Int
    ) {
        //prepare mock data
        mainPresenter.isCallingApi = false
        mainPresenter.isLoadingFinalPage = false
        val isShowProgressDialog = false
        MainPresenter.staticCurrentPage = currentPage
        var apiPageId: String? = null
        when (MainPresenter.staticCurrentPage) {
            0 -> apiPageId = GetArticlesService.API_PAGE_1
            1 -> apiPageId = GetArticlesService.API_PAGE_2
            2 -> apiPageId = GetArticlesService.API_PAGE_3
            3 -> apiPageId = GetArticlesService.API_PAGE_4
            4 -> apiPageId = GetArticlesService.API_PAGE_5
        }
        //when
        `when`(mainPresenter.connectivityReceiver.isNetworkConnected).thenReturn(true)

        val mainObserverMock = mock(MainObserver::class.java)
        `when`(mainObserverMock.presenter).thenReturn(mainPresenter)

        `when`(mainPresenter.provideMainObserver()).thenReturn(mainObserverMock)

        val newsList = NewsList(currentPage, 0, 0, ArrayList())
        `when`(
            mockGetArticlesService.execute(
                any(MainObserver::class.java),
                ArgumentMatchers.anyString()
            )
        ).then {
            if (currentPage == 4) {
                //hardcode to test for case false
                mainObserverMock.onError(Throwable())
            } else {
                mainObserverMock.onSuccess(newsList)
            }
        }

        doNothing().`when`(mainPresenter).onSuccessGetArticlesListApi(any(NewsList::class.java))
        //run
        mainPresenter.callGetArticlesListApi(isShowProgressDialog)
        //verify
        val mockMainViewInOrder = inOrder(mockMainView)
        mockMainViewInOrder.verify(mockMainView, times(1)).lockOrientation()
        mockMainViewInOrder.verify(mockMainView, never()).showProgressDialog()

        val mockGetArticlesServiceInOrder = inOrder(mockGetArticlesService)
        mockGetArticlesServiceInOrder.verify(mockGetArticlesService).dispose()
        mockGetArticlesServiceInOrder.verify(mockGetArticlesService)
            .execute(mainObserverMock, apiPageId)

        if (currentPage == 4) {
            verify(mainObserverMock, times(1)).onError(any(Throwable::class.java))
        } else {
            verify(mainObserverMock, times(1)).onSuccess(any(NewsList::class.java))
        }

        Assert.assertTrue(mainPresenter.isCallingApi)
        Assert.assertFalse(mainPresenter.isLoadingFinalPage)
    }

    @Test
    fun test_onSuccessGetArticlesListApi_currentPageEquals5() {
        test_onSuccessGetArticlesListApi(5)
    }

    @Test
    fun test_onSuccessGetArticlesListApi_currentPageNotEquals5() {
        test_onSuccessGetArticlesListApi(0)
    }

    private fun test_onSuccessGetArticlesListApi(currentPage: Int) {
        //prepare mock data
        val articlesList = ArrayList<Article>()
        articlesList.add(Article())
        val newsList = NewsList(currentPage, 0, 0, articlesList)
        mainPresenter.isLoadingFinalPage = false
        MainPresenter.staticArticlesList = spy(ArrayList())
        //when
        `when`(mockMainView.getTextSearch()).thenReturn("text_search")
        doNothing().`when`(mainPresenter).filterArticlesList(ArgumentMatchers.anyString())
        //run
        mainPresenter.onSuccessGetArticlesListApi(newsList)
        //verify
        Assert.assertTrue(MainPresenter.staticCurrentPage == currentPage)
        Assert.assertEquals(currentPage == 5, mainPresenter.isLoadingFinalPage)
        verify(mockMainView, times(1)).removeBottomProgressBar(false)

        verify(MainPresenter.staticArticlesList, times(1)).addAll(newsList.articles)
        Assert.assertTrue(MainPresenter.staticArticlesList.size == 1)

        verify(mockMainView, times(1)).getTextSearch()
        verify(mainPresenter, times(1)).filterArticlesList("text_search")
        verifyNoMoreInteractions(mockMainView)
    }

    @Test
    fun test_onError() {
        //prepare mock data
        mainPresenter.isCallingApi = true
        //run
        mainPresenter.onError()
        //verify
        verify(mockMainView).hideProgressDialog()
        verify(mockMainView).showToastError()
        verify(mockMainView).removeBottomProgressBar()
        verify(mockMainView).unlockOrientation()
        verifyNoMoreInteractions(mockMainView)
        Assert.assertFalse(mainPresenter.isCallingApi)
    }

    @Test
    fun test_filterArticlesList_textNull() {
        //prepare mock data
        MainPresenter.staticArticlesList = ArrayList()
        //when
        doNothing().`when`(mainPresenter).onFilterSuccess(MainPresenter.staticArticlesList)
        //run
        mainPresenter.filterArticlesList(null)
        //verify
        verify(mainPresenter).onFilterSuccess(MainPresenter.staticArticlesList)
    }

    @Test
    fun test_filterArticlesList_textEmpty() {
        //prepare mock data
        MainPresenter.staticArticlesList = ArrayList()
        //when
        doNothing().`when`(mainPresenter).onFilterSuccess(MainPresenter.staticArticlesList)
        //run
        mainPresenter.filterArticlesList("")
        //verify
        verify(mainPresenter).onFilterSuccess(MainPresenter.staticArticlesList)
    }

    inline fun <reified T> anyNonNull(): T = Mockito.any<T>(T::class.java)

    @Test
    fun test_filterArticlesList_textNotEmpty_filterSuccess() {
        //prepare mock data
        MainPresenter.staticArticlesList = ArrayList()
        MainPresenter.staticArticlesList.add(Article().apply { title = "test" })

        mainPresenter.isLoadingFinalPage = false
        mainPresenter.isCallingApi = true
        //when
        //TODO cannot use any() at here
        //doNothing().`when`(mainPresenter).onFilterSuccess(any())
        //run
        mainPresenter.filterArticlesList("test")
        //verify
        //TODO cannot use any() at here
        //verify(mainPresenter).onFilterSuccess(any())
        //Assert.assertFalse(mainPresenter.isCallingApi)
    }


    @Test
    fun test_onFilterSuccess_resultEmpty() {
        test_onFilterSuccess(ArrayList())
    }

    @Test
    fun test_onFilterSuccess_sizeOfArticlesListLessThan4() {
        val articlesList: ArrayList<Article> = ArrayList()
        articlesList.add(Article())
        articlesList.add(Article())
        articlesList.add(Article())
        test_onFilterSuccess(articlesList)
    }

    private fun test_onFilterSuccess(articlesList: ArrayList<Article>) {
        //prepare mock data
        mainPresenter.isLoadingFinalPage = false
        mainPresenter.isCallingApi = true
        //
        doNothing().`when`(mainPresenter).callGetArticlesListApi(true)
        //run
        mainPresenter.onFilterSuccess(articlesList)
        //verify
        Assert.assertFalse(mainPresenter.isCallingApi)
        verify(mainPresenter, times(1)).callGetArticlesListApi(true)
    }

    @Test
    fun test_onFilterSuccess_4Article() {
        //prepare mock data
        mainPresenter.isLoadingFinalPage = false
        mainPresenter.isCallingApi = true
        MainPresenter.staticArticlesListFiltered = ArrayList()
        val articlesList = ArrayList<Article>()
        articlesList.add(Article())
        articlesList.add(Article())
        articlesList.add(Article())
        articlesList.add(Article())
        //when
        doNothing().`when`(mockMainView)
            .onFilterListSuccess(MainPresenter.staticArticlesListFiltered)
        //run
        mainPresenter.onFilterSuccess(articlesList)
        //verify
        Assert.assertEquals(articlesList, MainPresenter.staticArticlesListFiltered)
        //
        verify(mockMainView, times(1)).onFilterListSuccess(MainPresenter.staticArticlesListFiltered)
        verify(mockMainView, times(1)).hideProgressDialog()
        verify(mockMainView, times(1)).unlockOrientation()
        verifyNoMoreInteractions(mockMainView)
        Assert.assertFalse(mainPresenter.isCallingApi)
        Assert.assertFalse(mainPresenter.isLoadingFinalPage)
    }

    @Test
    fun test_getArticlesListFiltered() {
        Assert.assertEquals(
            mainPresenter.getArticlesListFiltered(),
            MainPresenter.staticArticlesListFiltered
        )
    }

    @Test
    fun test_detachView() {
        Assert.assertNotNull(mainPresenter.getView())
        //run
        mainPresenter.detachView()
        //verify
        Assert.assertNull(mainPresenter.getView())
    }
    // Given
    /* val error = "Test error"
     val single: Single<NewsList> = Single.create { emitter ->
         emitter.onError(Exception(error))
     }*/
}