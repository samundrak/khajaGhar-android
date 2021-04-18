package com.app.khajaghar.ui.payment

import androidx.lifecycle.*
import com.app.khajaghar.data.local.Resource
import com.app.khajaghar.data.model.PlaceOrderRequest
import com.app.khajaghar.data.model.Response
import com.app.khajaghar.data.model.VerifyOrderResponse
import com.app.khajaghar.data.retrofit.OrderRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class PaymentViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    //verify order
    private val insertOrder = MutableLiveData<Resource<Response<VerifyOrderResponse>>>()
    val insertOrderStatus: LiveData<Resource<Response<VerifyOrderResponse>>>
        get() = insertOrder

    fun placeOrder(placeOrderRequest: PlaceOrderRequest) {
        viewModelScope.launch {
            try {
                insertOrder.value = Resource.loading()
                val response = orderRepository.insertOrder(placeOrderRequest)
                if(response!=null){
                    if(response.code==1){
                        insertOrder.value = Resource.success(response)
                    }else{
                        insertOrder.value = Resource.error(null,response.message)
                    }
                }
            } catch (e: Exception) {
                println("verify order failed ${e.message}")
                if (e is UnknownHostException) {
                    insertOrder.value = Resource.offlineError()
                } else {
                    insertOrder.value = Resource.error(e)
                }
            }
        }
    }

}