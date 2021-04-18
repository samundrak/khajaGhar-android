package com.app.khajaghar.data.local

interface AppPreferencesHelper {

    val name: String?
    val email: String?
    val mobile: String?
    val role: String?
    val oauthId: String?
    val token: String?
    val userId: String?
    val fcmToken: String?
    val place: String?
    val cart: String?
    val shopList: String?
    val cartShop: String?
    val cartDeliveryPref: String?
    val cartShopInfo: String?
    val cartDeliveryLocation: String?
    val tempMobile: String?
    val tempOauthId: String?
    val firstName: String?
    val lastName: String?
    val photo: String?

    fun saveUser(userId: String?, firstName: String?, lastName: String?, name: String?, email: String?, role: String?, oauthId: String?, place: String?, photo: String?)

    fun clearPreferences()

    fun clearCartPreferences()

}