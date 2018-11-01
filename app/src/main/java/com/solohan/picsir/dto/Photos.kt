package com.solohan.picsir.dto

import com.google.gson.annotations.SerializedName

data class Photos(
    @SerializedName("page") val page: Int,
    @SerializedName("pages") val pages: Int,
    @SerializedName("perpage") val perpage: Int,
    @SerializedName("total") val total: String,
    @SerializedName("photo") val photo: List<Photo>
)