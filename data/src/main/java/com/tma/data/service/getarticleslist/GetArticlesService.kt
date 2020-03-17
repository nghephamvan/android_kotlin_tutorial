package com.tma.data.service.getarticleslist

import com.google.gson.annotations.SerializedName
import com.tma.data.model.NewsList
import com.tma.data.service.BaseService
import io.reactivex.Scheduler
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import javax.inject.Inject

interface GetArticlesApi {
    @GET("bins/{pageNumber}")
    fun getArticlesList(@Path("pageNumber") pageNumber: String): Single<GetArticlesApiResponse>
}

class GetArticlesService @Inject constructor(
    private val getArticlesApi: GetArticlesApi,
    subscribeScheduler: Scheduler,
    postExecutionScheduler: Scheduler
) : BaseService<NewsList, Any>(subscribeScheduler, postExecutionScheduler) {

    companion object {
        const val API_PAGE_1 = "12vbq7"
        const val API_PAGE_2 = "149cql"
        const val API_PAGE_3 = "npi4d"
        const val API_PAGE_4 = "buz6l"
        const val API_PAGE_5 = "efcst"
    }

    override fun buildServiceSingle(params: Any?): Single<NewsList> {
        return getArticlesApi.getArticlesList(params as String).map { it.newsList }
    }
}

data class GetArticlesApiResponse(
    @SerializedName("NewsList")
    val newsList: NewsList
)