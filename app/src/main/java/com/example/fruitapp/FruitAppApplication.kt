package com.example.fruitapp;

import android.app.Application
import com.example.fruitapp.data.AppContainer
import com.example.fruitapp.data.DefaultAppContainer

class FruitAppApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer()
    }
}
