package com.example.terminmenadzer

import android.app.Application

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        com.example.terminmenadzer.data.DatabaseProvider.init(applicationContext)
    }
}