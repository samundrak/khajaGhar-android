package com.app.khajaghar.data.model

import com.google.gson.annotations.SerializedName

data class MenuItemModel(
        @SerializedName("category")
        val category: String,
        @SerializedName("_id")
        val id: String,
        @SerializedName("isAvailable")
        val isAvailable: Int,
        @SerializedName("isVeg")
        val isVeg: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("images")
        val images: ArrayList<String>,
        @SerializedName("photoUrl")
        val photoUrl: String,
        @SerializedName("price")
        val price: Int,
        @SerializedName("shopModel")
        val shopModel: ShopModel?,
        var quantity: Int = 0,
        var shopId: Int?,
        var shopName: String?,
        var isDish: Boolean = true
)
