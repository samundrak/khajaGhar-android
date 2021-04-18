package com.app.khajaghar.ui.profile

import android.content.Context
import androidx.lifecycle.*
import com.app.khajaghar.data.local.PreferencesHelper
import com.app.khajaghar.data.local.Resource
import com.app.khajaghar.data.model.*
import com.app.khajaghar.data.retrofit.PlaceRepository
import com.app.khajaghar.data.retrofit.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.launch
import java.io.File

import java.net.UnknownHostException


class ProfileViewModel(
        private val userRepository: UserRepository, private val placeRepository: PlaceRepository,
        private val preferencesHelper: PreferencesHelper
) : ViewModel() {

    //Fetch places list
    private val performFetchPlacesList = MutableLiveData<Resource<Response<List<PlaceModel>>>>()
    val performFetchPlacesStatus: LiveData<Resource<Response<List<PlaceModel>>>>
        get() = performFetchPlacesList


    //Update User Details
    private val performUpdate = MutableLiveData<Resource<String>>()
    val performUpdateStatus: LiveData<Resource<String>>
        get() = performUpdate

    fun updateUserDetails(user: User, file: File?) {
        viewModelScope.launch {
//            try {
                performUpdate.value = Resource.loading()
                val response = userRepository.updateUser(user, file)
                performUpdate.value = Resource.success("Updated")
//            } catch (e: Exception) {
//                println("update user details failed ${e.message}")
//                if (e is UnknownHostException) {
//                    performUpdate.value = Resource.offlineError()
//                } else {
//                    performUpdate.value = Resource.error(e)
//                }
//            }
        }
    }

    //Update User FCM token
    private val performNotificationTokenUpdate = MutableLiveData<Resource<Response<String>>>()
    val performNotificationTokenUpdateStatus: LiveData<Resource<Response<String>>>
        get() = performNotificationTokenUpdate

    fun updateFcmToken(notificationTokenUpdate: NotificationTokenUpdate) {
        viewModelScope.launch {
            try {
                performNotificationTokenUpdate.value = Resource.loading()
                val response = userRepository.updateFcmToken(notificationTokenUpdate)
                if (response.code == 1) {
                    if (response.data != null) {
                        performNotificationTokenUpdate.value = Resource.success(response)
                    } else {
                        performNotificationTokenUpdate.value = Resource.error(null, message = "Something went wrong")
                    }
                } else {
                    performNotificationTokenUpdate.value = Resource.error(null, message = response.message)
                }
            } catch (e: Exception) {
                println("update fcm token failed ${e.message}")
                if (e is UnknownHostException) {
                    performNotificationTokenUpdate.value = Resource.offlineError()
                } else {
                    performNotificationTokenUpdate.value = Resource.error(e)
                }
            }
        }
    }


    private val verifyOtp = MutableLiveData<Resource<String>>()
    val verifyOtpStatus: LiveData<Resource<String>>
        get() = verifyOtp

    fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, context: Context) {

        var auth = FirebaseAuth.getInstance()
        verifyOtp.value = Resource.loading()

        auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->

                    viewModelScope.launch {
                        if (task.isSuccessful) {
                            val user = task.result?.user
                            preferencesHelper.tempOauthId = user?.uid
                            preferencesHelper.tempMobile = user?.phoneNumber?.substring(3)
                            verifyOtp.value = Resource.success("")
                        } else {
                            verifyOtp.value = Resource.error(message = "")
                        }
                    }

                }

    }


}