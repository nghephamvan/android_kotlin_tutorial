package com.tma.data.model

import com.google.gson.annotations.SerializedName

class Article(
    @SerializedName("ID")
    var id: Long,
    @SerializedName("Title")
    var title: String,
    @SerializedName("ImageUrl")
    var imageUrl: String,
    @SerializedName("LastModifiedDate")
    var lastModifiedDate: String,
    @SerializedName("Content")
    var content: String,

    var portraitContent: String?
) {
    constructor() : this(-1, "", "", "", "", null)
}