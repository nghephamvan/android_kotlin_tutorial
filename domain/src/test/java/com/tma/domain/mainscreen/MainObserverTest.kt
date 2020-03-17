package com.tma.domain.mainscreen

import com.tma.data.model.NewsList
import com.tma.domain.mainscreen.MainObserver
import com.tma.domain.mainscreen.MainPresenter
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class MainObserverTest {

    @Mock
    lateinit var mainPresenter: MainPresenter

    @InjectMocks
    lateinit var mainObserver: MainObserver

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun test_onSuccess() {
        //prepare mock data
        val newsList = NewsList(0, 1, 1, ArrayList())
        //run
        mainObserver.onSuccess(newsList)
        //verify
        verify(mainPresenter, times(1)).onSuccessGetArticlesListApi(newsList)
        verifyNoMoreInteractions(mainPresenter)
    }

    @Test
    fun test_onError() {
        //run
        mainObserver.onError(Throwable())
        //verify
        verify(mainPresenter).onError()
    }
}