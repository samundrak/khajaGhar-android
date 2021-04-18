package com.app.khajaghar.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.app.khajaghar.data.model.*
import com.app.khajaghar.utils.AppConstants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesHelper(context: Context) : AppPreferencesHelper {

    private val loginPreferences: SharedPreferences =
            context.getSharedPreferences(AppConstants.LOGIN_PREFS, MODE_PRIVATE)
    private val customerPreferences: SharedPreferences =
            context.getSharedPreferences(AppConstants.CUSTOMER_PREFS, MODE_PRIVATE)
    private val cartPreferences: SharedPreferences =
            context.getSharedPreferences(AppConstants.CART_PREFERENCES, MODE_PRIVATE)

    override var name: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_NAME, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_NAME, value).apply()
    override var firstName: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_FIRST_NAME, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_FIRST_NAME, value).apply()
    override var lastName: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_LAST_NAME, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_LAST_NAME, value).apply()

    override var photo: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_PHOTO, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_PHOTO, value).apply()


    override var email: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_EMAIL, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_EMAIL, value).apply()

    override var mobile: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_MOBILE, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_MOBILE, value).apply()

    override var role: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_ROLE, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_ROLE, value).apply()

    override var oauthId: String?
        get() = loginPreferences.getString(AppConstants.AUTH_TOKEN, null)
        set(value) = loginPreferences.edit().putString(AppConstants.AUTH_TOKEN, value).apply()

    override var userId: String?
        get() = loginPreferences.getString(AppConstants.USER_ID, "")
        set(value) = loginPreferences.edit().putString(AppConstants.USER_ID, value!!).apply()

    override var token: String?
        get() = loginPreferences.getString(AppConstants.TOKEN, "")
        set(value) = loginPreferences.edit().putString(AppConstants.TOKEN, value!!).apply()

    override var fcmToken: String?
        get() = loginPreferences.getString(AppConstants.FCM_TOKEN, null)
        set(value) = loginPreferences.edit().putString(AppConstants.FCM_TOKEN, value).apply()

    override var place: String?
        get() = customerPreferences.getString(AppConstants.CUSTOMER_PLACE, null)
        set(value) = customerPreferences.edit().putString(AppConstants.CUSTOMER_PLACE, value).apply()

    override var shopList: String?
        get() = customerPreferences.getString(AppConstants.SHOP_LIST, null)
        set(value) = customerPreferences.edit().putString(AppConstants.SHOP_LIST, value).apply()

    override var cart: String?
        get() = cartPreferences.getString(AppConstants.CART, null)
        set(value) = cartPreferences.edit().putString(AppConstants.CART, value).apply()

    override var cartShop: String?
        get() = cartPreferences.getString(AppConstants.CART_SHOP, null)
        set(value) = cartPreferences.edit().putString(AppConstants.CART_SHOP, value).apply()

    override var cartDeliveryPref: String?
        get() = cartPreferences.getString(AppConstants.CART_DELIVERY, null)
        set(value) = cartPreferences.edit().putString(AppConstants.CART_DELIVERY, value).apply()

    override var cartShopInfo: String?
        get() = cartPreferences.getString(AppConstants.CART_SHOP_INFO, null)
        set(value) = cartPreferences.edit().putString(AppConstants.CART_SHOP_INFO, value).apply()

    override var cartDeliveryLocation: String?
        get() = cartPreferences.getString(AppConstants.CART_DELIVERY_LOCATION, null)
        set(value) = cartPreferences.edit().putString(AppConstants.CART_DELIVERY_LOCATION, value).apply()

    override var tempMobile: String?
        get() = customerPreferences.getString(AppConstants.TEMP_MOBILE, null)
        set(value) = customerPreferences.edit().putString(AppConstants.TEMP_MOBILE, value).apply()

    override var tempOauthId: String?
        get() = customerPreferences.getString(AppConstants.TEMP_OAUTHID, null)
        set(value) = customerPreferences.edit().putString(AppConstants.TEMP_OAUTHID, value).apply()

    override fun saveUser(userId: String?, firstName: String?, lastName: String?, name: String?, email: String?, role: String?, oauthId: String?, place: String?, photo: String?) {
        customerPreferences.edit().putString(AppConstants.CUSTOMER_FIRST_NAME, firstName).apply()
        customerPreferences.edit().putString(AppConstants.CUSTOMER_LAST_NAME, lastName).apply()
        customerPreferences.edit().putString(AppConstants.CUSTOMER_NAME, "$firstName $lastName").apply()
        customerPreferences.edit().putString(AppConstants.CUSTOMER_EMAIL, email).apply()
        customerPreferences.edit().putString(AppConstants.CUSTOMER_MOBILE, mobile).apply()
        customerPreferences.edit().putString(AppConstants.CUSTOMER_ROLE, role).apply()
        loginPreferences.edit().putString(AppConstants.AUTH_TOKEN, oauthId).apply()
        customerPreferences.edit().putString(AppConstants.CUSTOMER_PHOTO, photo).apply()
        userId?.let { loginPreferences.edit().putString(AppConstants.USER_ID, it).apply() }
        customerPreferences.edit().putString(AppConstants.CUSTOMER_PLACE, place).apply()
    }

    override fun clearPreferences() {
        loginPreferences.edit().clear().apply()
        customerPreferences.edit().clear().apply()
        cartPreferences.edit().clear().apply()
    }

    override fun clearCartPreferences() {
        cartPreferences.edit().clear().apply()
    }

    fun getPlace(): PlaceModel? {
        return Gson().fromJson(place, PlaceModel::class.java)
    }

    fun getUser(): User {
        return User(userId, email, firstName, lastName, isCustomer = true)
    }

    fun getCart(): List<MenuItemModel>? {
        val listType = object : TypeToken<List<MenuItemModel?>?>() {}.type
        return Gson().fromJson(cart, listType)
    }

    fun getShopList(): List<ShopConfigurationModel>? {
        val listType = object : TypeToken<List<ShopConfigurationModel?>?>() {}.type
        return Gson().fromJson(shopList, listType)
    }

    fun getCartShop(): ShopConfigurationModel? {
        return Gson().fromJson(cartShop, ShopConfigurationModel::class.java)
    }

}