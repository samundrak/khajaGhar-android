package com.app.khajaghar.ui.placeorder

import android.animation.LayoutTransition.CHANGING
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.app.khajaghar.R
import com.app.khajaghar.data.local.PreferencesHelper
import com.app.khajaghar.data.local.Resource
import com.app.khajaghar.databinding.ActivityPlaceOrderBinding
import com.app.khajaghar.ui.home.HomeActivity
import com.app.khajaghar.ui.order.OrderDetailActivity
import com.app.khajaghar.utils.AppConstants
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*

class PlaceOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaceOrderBinding
    private val viewModel: PlaceOrderViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private var orderId: String? = null
    private var isOrderPlaced = false
    private var isOrderFailed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        initView()
        setListener()
        setObservers()
        orderId?.let {
            viewModel.placeOrder(it)
        } ?: run {
            binding.animationView.loop(false)
            binding.animationView.setAnimation("order_failed_animation.json")
        }
    }

    private fun getArgs() {
        orderId = intent.getStringExtra(AppConstants.ORDER_ID)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_place_order)
        binding.layoutState.layoutTransition.enableTransitionType(CHANGING)
    }

    private fun setListener() {
        binding.textGoHome.setOnClickListener {
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }
        binding.textViewOrder.setOnClickListener {
            val i = Intent(applicationContext, HomeActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            val j = Intent(applicationContext, OrderDetailActivity::class.java)
            j.putExtra(AppConstants.ORDER_ID,orderId)
            j.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
            startActivity(j)
        }
    }

    private fun setObservers() {
        viewModel.placeOrderStatus.observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Resource.Status.LOADING -> {
                    binding.animationView.loop(true)
                    binding.animationView.setAnimation("loading_animation.json")
                    binding.animationView.playAnimation()
                    binding.textPlaceOrderStatus.text = "Placing order"
                    isOrderFailed = true
                }
                Resource.Status.SUCCESS -> {
                    binding.animationView.loop(false)
                    binding.animationView.setAnimation("order_success_animation.json")
                    binding.animationView.playAnimation()
                    binding.textPlaceOrderStatus.text = "Order placed successfully"
                    isOrderPlaced = true
                    isOrderFailed = false
                    preferencesHelper.clearCartPreferences()
                    binding.layoutRedirection.visibility = View.VISIBLE
                }
                Resource.Status.OFFLINE_ERROR -> {
                    binding.animationView.loop(false)
                    binding.animationView.setAnimation("no_internet_connection_animation.json")
                    binding.animationView.playAnimation()
                    binding.textPlaceOrderStatus.text = "No internet connection"
                    isOrderFailed = true
                }
                Resource.Status.ERROR -> {
                    binding.animationView.loop(false)
                    binding.animationView.setAnimation("order_failed_animation.json")
                    binding.animationView.playAnimation()
                    if (it.data?.message.isNullOrEmpty()) {
                        binding.textPlaceOrderStatus.text = "Something went wrong. Please try again after some time!"
                    } else {
                        binding.textPlaceOrderStatus.text = it.data?.message
                    }

                    isOrderFailed = true
                }
            }
        })
    }

    override fun onBackPressed() {
        if (isOrderPlaced || isOrderFailed) {
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }
    }
}
