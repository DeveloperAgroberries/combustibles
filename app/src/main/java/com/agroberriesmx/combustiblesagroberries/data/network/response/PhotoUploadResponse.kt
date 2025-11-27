package com.agroberriesmx.combustiblesagroberries.data.network.response

import com.google.gson.annotations.SerializedName

data class PhotoUploadResponse(
    @SerializedName("message") // Maps the JSON key "message" to this property
    val message: String

    )