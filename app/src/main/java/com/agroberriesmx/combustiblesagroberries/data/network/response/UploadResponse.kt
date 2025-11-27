package com.agroberriesmx.combustiblesagroberries.data.network.response

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @SerializedName("message") val message: String
)