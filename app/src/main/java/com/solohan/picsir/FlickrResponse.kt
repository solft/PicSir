package com.solohan.picsir

import com.google.gson.annotations.SerializedName

data class FlickrResponse(
    @SerializedName("photos") val photos: Photos,
    @SerializedName("stat") val stat: String
)