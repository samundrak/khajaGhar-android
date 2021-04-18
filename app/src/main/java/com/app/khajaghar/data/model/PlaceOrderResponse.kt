package com.app.khajaghar.data.model

import com.google.gson.annotations.SerializedName

class PlaceOrderResponse(
        @SerializedName("shift")
        val shift: String,
        @SerializedName("_id")
        val id: String,
        @SerializedName("status")
        val status: String
)