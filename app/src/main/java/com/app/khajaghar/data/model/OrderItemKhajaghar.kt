package com.app.khajaghar.data.model

import com.google.gson.annotations.SerializedName

class OrderItemKhajaghar(
        @SerializedName("_id")
        val id: String,
        @SerializedName("status")
        val status: String,
        @SerializedName("shift")
        val shift: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("food")
        val food: MenuItemModel

)