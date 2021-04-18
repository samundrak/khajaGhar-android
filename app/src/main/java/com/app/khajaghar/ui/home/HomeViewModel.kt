package com.app.khajaghar.ui.home

import androidx.lifecycle.*
import com.app.khajaghar.data.local.Resource
import com.app.khajaghar.data.model.MenuItemModel
import com.app.khajaghar.data.model.PlaceOrderResponse
import com.app.khajaghar.data.model.User
import com.app.khajaghar.data.retrofit.ShopRepository
import com.app.khajaghar.data.retrofit.UserRepository
import kotlinx.coroutines.launch

import java.net.UnknownHostException


class HomeViewModel(private val shopRepository: ShopRepository, private val userRepository: UserRepository) : ViewModel() {

    //Fetch foods
    private val performFetchShops = MutableLiveData<Resource<List<MenuItemModel>>>()
    val performFetchShopsStatus: LiveData<Resource<List<MenuItemModel>>>
        get() = performFetchShops

    //profile fetch
    private val performProfileFetch = MutableLiveData<Resource<User>>()
    val performFetchProfileStatus: LiveData<Resource<User>>
        get() = performProfileFetch

    //profile fetch
    private val performPlaceOrder = MutableLiveData<Resource<PlaceOrderResponse>>()
    val performPlaceOrderStatus: LiveData<Resource<PlaceOrderResponse>>
        get() = performPlaceOrder


    fun getFood() {
        viewModelScope.launch {
            try {
                performFetchShops.value = Resource.loading()
                val response = shopRepository.getFood()
                if (!response.isNullOrEmpty()) {
                    val foods: ArrayList<MenuItemModel> = ArrayList()
                    foods.addAll(response)
//                        foods.sortByDescending {
//                            it.configurationModel.isOrderTaken
//                        }
                    performFetchShops.value = Resource.success(foods)
                } else {
                    performFetchShops.value = Resource.empty()
                }
            } catch (e: Exception) {
                println("fetch foods failed ${e.message}")
                if (e is UnknownHostException) {
                    performFetchShops.value = Resource.offlineError()
                } else {
                    performFetchShops.value = Resource.error(e)
                }
            }
        }
    }

    fun getUser() {
        viewModelScope.launch {
            try {
                performProfileFetch.value = Resource.loading()
                val response = userRepository.getUser()
                performProfileFetch.value = Resource.success(response)

            } catch (e: Exception) {
                println("fetch foods failed ${e.message}")
                if (e is UnknownHostException) {
                    performProfileFetch.value = Resource.offlineError()
                } else {
                    performProfileFetch.value = Resource.error(e)
                }
            }
        }
    }

    fun placeOrder(item: MenuItemModel, shift: String) {
        viewModelScope.launch {
            try {
                performPlaceOrder.value = Resource.loading()
                val response = shopRepository.placeOrder(item,shift)
                performPlaceOrder.value = Resource.success(response)

            } catch (e: Exception) {
                println("fetch foods failed ${e.message}")
                if (e is UnknownHostException) {
                    performPlaceOrder.value = Resource.offlineError()
                } else {
                    performPlaceOrder.value = Resource.error(e)
                }
            }
        }
    }


}