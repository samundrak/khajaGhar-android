package com.app.khajaghar.ui.login

import androidx.lifecycle.*
import com.app.khajaghar.data.local.Resource
import com.app.khajaghar.data.model.LoginRequest
import com.app.khajaghar.data.model.LoginResponse
import com.app.khajaghar.data.model.Response
import com.app.khajaghar.data.model.UserPlaceModel
import com.app.khajaghar.data.retrofit.UserRepository
import kotlinx.coroutines.launch

import java.net.UnknownHostException

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    //LOGIN
    private val performLogin = MutableLiveData<Resource<LoginResponse>>()
    val performLoginStatus: LiveData<Resource<LoginResponse>>
        get() = performLogin

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            try {
                performLogin.value = Resource.loading()
                val response = userRepository.login(loginRequest)
                performLogin.value = Resource.success(response)
            } catch (e: Exception) {
                println("login failed ${e.message}")
                if (e is UnknownHostException) {
                    performLogin.value = Resource.offlineError()
                } else {
                    performLogin.value = Resource.error(e)
                }
            }
        }
    }

}