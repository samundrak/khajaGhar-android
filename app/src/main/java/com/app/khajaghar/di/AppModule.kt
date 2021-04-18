package com.app.khajaghar.di

import com.google.gson.Gson
import com.app.khajaghar.data.local.PreferencesHelper
import org.koin.dsl.module

val appModule = module {

    single {
        Gson()
    }

    single {
        PreferencesHelper(get())
    }

}