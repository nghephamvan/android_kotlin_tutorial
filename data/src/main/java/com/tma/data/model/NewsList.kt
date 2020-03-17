package com.tma.data.model

import com.google.gson.annotations.SerializedName

class NewsList (
    @SerializedName("CurrentPage")
    var currentPage: Int,
    @SerializedName("ItemsPerPage")
    var itemsPerPage: Int,
    @SerializedName("TotalPages")
    var totalPages: Int,
    @SerializedName("Articles")
    var articles: ArrayList<Article>
)