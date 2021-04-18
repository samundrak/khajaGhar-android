package com.app.khajaghar.data.retrofit

import com.app.khajaghar.data.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface CustomApi {

    //USER REPO
    @POST("/api/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @Multipart
    @PATCH("/api/auth/users/{userId}") //This can be used for both sign-up and updating profile
    suspend fun updateUser(
            @Path("userId") userId: String, @Part photo: MultipartBody.Part?, @Part("first_name") firstName: RequestBody,
            @Part("last_name") lastName: RequestBody
    ): retrofit2.Response<Unit>

    @POST("/api/register") //This can be used for both sign-up and updating profile
    suspend fun signup(@Body user: User): RegisterResponse

    @GET("/api/auth/profile") //This can be used for both sign-up and updating profile
    suspend fun getUser(): User

    @PATCH("/user/notif")
    suspend fun updateFcmToken(@Body notificationTokenUpdateModel: NotificationTokenUpdate): Response<String>

    //SHOP REPO
    @GET("/shop/place/{placeId}")
    suspend fun getShops(@Path("placeId") placeId: String): Response<List<ShopConfigurationModel>>

    //SHOP REPO
    @GET("/api/auth/foods")
    suspend fun getFood(): List<MenuItemModel>

    //PLACE REPO
    @GET("/place")
    suspend fun getPlaceList(): Response<List<PlaceModel>>

    //ITEM REPO
    @GET("/menu/{placeId}/{query}")
    suspend fun searchItems(@Path("placeId") placeId: String, @Path("query") query: String): Response<List<MenuItemModel>>

    @GET("/menu/shop/{shopId}")
    suspend fun getMenu(@Path("shopId") shopId: String): Response<List<MenuItemModel>>

    //ORDER REPO
    @GET("/order/{orderId}")
    suspend fun getOrderById(@Path("orderId") orderId: Int): Response<OrderItemListModel>

    @GET("/order/customer/{userId}/{pageNum}/{pageCount}")
    suspend fun getOrders(
            @Path("userId") id: String,
            @Path("pageNum") pageNum: Int,
            @Path("pageCount") pageCount: Int
    ): Response<List<OrderItemListModel>>

    @GET("/api/auth/orders")
    suspend fun getOrdersFromKhajaghar(): List<OrderItemKhajaghar>

    @POST("/order")
    suspend fun insertOrder(@Body placeOrderRequest: PlaceOrderRequest): Response<VerifyOrderResponse>

    @POST("/order/place/{orderId}")
    suspend fun placeOrder(@Path("orderId") orderId: String): Response<String>


    @POST("/api/auth/orders")
    suspend fun placeOrderForKhajaGhar(@Body order: PlaceOrderRequestKhajaGhar): PlaceOrderResponse


    @PATCH("/order/rating")
    suspend fun rateOrder(@Body ratingRequest: RatingRequest): Response<String>

    @PATCH("/order/status")
    suspend fun cancelOrder(@Body orderStatusRequest: OrderStatusRequest): Response<String>


}