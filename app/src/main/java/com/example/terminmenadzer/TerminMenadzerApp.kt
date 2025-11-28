package com.example.terminmenadzer

import android.app.Application

class TerminMenadzerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseProvider.init(applicationContext)
    }
}