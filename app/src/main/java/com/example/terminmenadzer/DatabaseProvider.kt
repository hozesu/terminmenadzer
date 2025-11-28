package com.example.terminmenadzer

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    lateinit var db: AppDatabase
        private set

    fun init(context: Context) {
        db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "termin_menadzer_db"
        ).build()
    }
}