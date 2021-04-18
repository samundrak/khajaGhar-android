package com.app.khajaghar.ui.order

import com.app.khajaghar.data.model.OrderStatusModel

data class OrderStatus(
        var isDone: Boolean = false,
        var isCurrent: Boolean = false,
        var name: String,
        var orderStatusList: List<OrderStatusModel> = listOf()
)
