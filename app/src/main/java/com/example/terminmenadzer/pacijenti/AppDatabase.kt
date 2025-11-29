package com.example.terminmenadzer.pacijenti

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PacijentEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pacijentDao(): PacijentDao
}