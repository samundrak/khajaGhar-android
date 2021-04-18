package com.app.khajaghar.data.retrofit

import com.app.khajaghar.data.model.MenuItemModel
import com.app.khajaghar.data.model.PlaceOrderRequestKhajaGhar
import retrofit2.Retrofit

class ShopRepository(retrofit: Retrofit) {
    private val services = retrofit.create(CustomApi::class.java)
    suspend fun getShops(placeId: String) = services.getShops(placeId)
    suspend fun getFood() = services.getFood()
    suspend fun placeOrder(item: MenuItemModel, shift: String) = services.placeOrderForKhajaGhar(PlaceOrderRequestKhajaGhar(foodId = item.id, shift = shift))

}