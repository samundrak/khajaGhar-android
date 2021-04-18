package com.app.khajaghar.data.model

import com.google.gson.annotations.SerializedName

data class User(
        @SerializedName("_id")
        val userId: String? = null,
        @SerializedName("email")
        val email: String? = null,
        @SerializedName("first_name")
        val firstName: String? = null,
        @SerializedName("last_name")
        val lastName: String? = null,
        @SerializedName("password")
        val password: String? = null,
        @SerializedName("status")
        var status: Int? = null,
        @SerializedName("isCustomer")
        var isCustomer: Boolean? = null,
        @SerializedName("image")
        var photo: String? = null


)