package com.app.khajaghar.data.retrofit

import com.app.khajaghar.data.model.OrderStatusRequest
import com.app.khajaghar.data.model.PlaceOrderRequest
import com.app.khajaghar.data.model.RatingRequest
import retrofit2.Retrofit

class OrderRepository(retrofit: Retrofit) {
    private val services = retrofit.create(CustomApi::class.java)
    suspend fun getOrderById(orderId: Int) = services.getOrderById(orderId)
    suspend fun getOrders(id: String, pageNum: Int, pageCount: Int) = services.getOrders(id, pageNum, pageCount)
    suspend fun getOrdersFromKhajaghar(id: String, pageNum: Int, pageCount: Int) = services.getOrdersFromKhajaghar()
    suspend fun insertOrder(placeOrderRequest: PlaceOrderRequest) = services.insertOrder(placeOrderRequest)
    suspend fun placeOrder(orderId: String) = services.placeOrder(orderId)
    suspend fun rateOrder(ratingRequest: RatingRequest) = services.rateOrder(ratingRequest)
    suspend fun cancelOrder(orderStatusRequest: OrderStatusRequest) = services.cancelOrder(orderStatusRequest)
}