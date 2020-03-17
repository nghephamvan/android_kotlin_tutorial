package com.tma.domain.mainscreen

import com.tma.data.model.Article
import com.tma.domain.mainscreen.FilterObserver
import com.tma.domain.mainscreen.MainPresenter
import com.tma.domain.mainscreen.MainView
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class FilterObserverTest {

    @Mock
    lateinit var mockMainView: MainView

    @Mock
    lateinit var mainPresenter: MainPresenter

    @InjectMocks
    lateinit var filterObserver: FilterObserver

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun test_onSuccess() {
        //prepare mock data
        val articlesList: List<Article> = ArrayList()
        //run
        filterObserver.onSuccess(articlesList)
        //verify
        verify(mainPresenter, times(1)).onFilterSuccess(articlesList as ArrayList<Article>)
        verifyNoMoreInteractions(mainPresenter)
    }

    @Test
    fun test_onError() {
        //prepare mock data
        mainPresenter.isCallingApi = true
        //
        `when`(mainPresenter.getView()).thenReturn(mockMainView)
        //run
        filterObserver.onError(Throwable())
        //verify
        val mockMainViewInOrder = inOrder(mockMainView)
        mockMainViewInOrder.verify(mockMainView, times(1)).hideProgressDialog()
        mockMainViewInOrder.verify(mockMainView, times(1)).showToastError()
        mockMainViewInOrder.verify(mockMainView, times(1)).removeBottomProgressBar()
        mockMainViewInOrder.verify(mockMainView, times(1)).unlockOrientation()
        verifyNoMoreInteractions(mockMainView)
        Assert.assertFalse(mainPresenter.isCallingApi)
    }
}