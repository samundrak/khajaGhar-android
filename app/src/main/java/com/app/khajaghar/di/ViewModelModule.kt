package com.app.khajaghar.di

import com.app.khajaghar.ui.cart.CartViewModel
import com.app.khajaghar.ui.contributors.ContributorViewModel
import com.app.khajaghar.ui.home.HomeViewModel
import com.app.khajaghar.ui.login.LoginViewModel
import com.app.khajaghar.ui.order.OrderViewModel
import com.app.khajaghar.ui.otp.OtpViewModel
import com.app.khajaghar.ui.payment.PaymentViewModel
import com.app.khajaghar.ui.placeorder.PlaceOrderViewModel
import com.app.khajaghar.ui.profile.ProfileViewModel
import com.app.khajaghar.ui.restaurant.RestaurantViewModel
import com.app.khajaghar.ui.search.SearchViewModel
import com.app.khajaghar.ui.signup.SignUpViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(),get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { OtpViewModel(get()) }
    viewModel { RestaurantViewModel(get()) }
    viewModel { SignUpViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get(),get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { OrderViewModel(get()) }
    viewModel { CartViewModel(get()) }
    viewModel { PlaceOrderViewModel(get()) }
    viewModel { PaymentViewModel(get()) }
    viewModel { ContributorViewModel() }
}