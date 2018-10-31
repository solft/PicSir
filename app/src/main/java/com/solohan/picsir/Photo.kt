package com.solohan.picsir

import com.google.gson.annotations.SerializedName

data class Photo(
        @SerializedName("id") val id: String = "",
        @SerializedName("secret") val secret: String = "",
        @SerializedName("server") val server: String = "",
        @SerializedName("farm") val farm: Int = 0,
        @SerializedName("title") val title: String = ""
){
    fun getImgUrl() = "https://farm$farm.staticflickr.com/$server/${id}_$secret"
}