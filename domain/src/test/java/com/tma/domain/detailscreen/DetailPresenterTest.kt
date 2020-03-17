package com.tma.domain.detailscreen

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.MockitoAnnotations

class DetailPresenterTest {
    @Mock
    lateinit var mockDetailView: DetailView

    private lateinit var detailPresenter: DetailPresenter

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        detailPresenter = spy(DetailPresenter())
        detailPresenter.attachView(mockDetailView)
    }

    @Test
    fun test_initialise() {
        //run
        detailPresenter.initialise()
        //verify
        verifyNoInteractions(mockDetailView)
    }

    @Test
    fun test_disposeSubscriptions() {
        //run
        detailPresenter.disposeSubscriptions()
        //verify
        verifyNoInteractions(mockDetailView)
    }

    @Test
    fun test_detachView() {
        Assert.assertNotNull(detailPresenter.getView())
        //run
        detailPresenter.detachView()
        //verify
        Assert.assertNull(detailPresenter.getView())
    }
}