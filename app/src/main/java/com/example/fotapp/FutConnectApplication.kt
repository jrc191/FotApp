package com.example.fotapp

import android.app.Application
import com.example.fotapp.di.AppContainer
import com.example.fotapp.di.DefaultAppContainer

class FutConnectApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
