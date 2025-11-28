package com.example.terminmenadzer.data

import android.content.Context
import androidx.room.Room

object DatabaseProvider {
    // Bazni objekat Room baze
    lateinit var db: AppDatabase
        private set

    // Inicijalizacija baze, pozivaš iz Application klase
    fun init(context: Context) {
        db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "termin_menadzer_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}