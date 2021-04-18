package com.app.khajaghar.data.model
import org.json.JSONObject

data class NotificationModel(
    var type: String?,
    var title: String?,
    var message: String?,
    var payload: JSONObject
)