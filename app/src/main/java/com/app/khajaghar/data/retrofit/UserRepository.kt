package com.app.khajaghar.data.retrofit

import com.app.khajaghar.data.model.LoginRequest
import com.app.khajaghar.data.model.NotificationTokenUpdate
import com.app.khajaghar.data.model.UpdateUserRequest
import com.app.khajaghar.data.model.User
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import java.io.File

class UserRepository(retrofit: Retrofit) {
    private val services = retrofit.create(CustomApi::class.java)
    suspend fun login(loginRequest: LoginRequest) = services.login(loginRequest)
    suspend fun updateUser(user: User, imageFile: File?) = services.updateUser(user.userId!!,
            photo = MultipartBody.Part.createFormData("photo", imageFile?.name,
                    RequestBody.create(MediaType.parse("image/*"), imageFile)),
            firstName = RequestBody.create(
                    MediaType.parse("multipart/form-data"), user.firstName),
            lastName = RequestBody.create(
                    MediaType.parse("multipart/form-data"), user.lastName)
            )

    suspend fun signup(user: User) = services.signup(user)
    suspend fun getUser() = services.getUser()
    suspend fun updateFcmToken(notificationTokenUpdate: NotificationTokenUpdate) = services.updateFcmToken(notificationTokenUpdate)

}