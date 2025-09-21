package com.example.recipebook


import android.app.Application
import com.example.recipebook.presentation.util.NetworkMonitor
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RecipeApplication : Application(){
    lateinit var networkMonitor: NetworkMonitor
        private set

    override fun onCreate() {
        super.onCreate()
        networkMonitor = NetworkMonitor(this)
        networkMonitor.register()
    }

    override fun onTerminate() {
        super.onTerminate()
        networkMonitor.unregister()
    }
}