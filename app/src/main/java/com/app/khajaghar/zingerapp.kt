package com.app.khajaghar

import android.app.Application
import com.app.khajaghar.di.appModule
import com.app.khajaghar.di.networkModule
import com.app.khajaghar.di.viewModelModule
import com.app.khajaghar.utils.PicassoImageLoadingService
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import ss.com.bannerslider.Slider

class zingerapp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@zingerapp)
            modules(listOf(appModule, networkModule, viewModelModule))
        }
        Slider.init(PicassoImageLoadingService(this))
    }

}