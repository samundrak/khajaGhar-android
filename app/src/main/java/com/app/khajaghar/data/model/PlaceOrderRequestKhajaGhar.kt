package com.app.khajaghar.data.model

import com.google.gson.annotations.SerializedName

class PlaceOrderRequestKhajaGhar(
        @SerializedName("food_id")
        val foodId: String,
        @SerializedName("shift")
        val shift: String
)