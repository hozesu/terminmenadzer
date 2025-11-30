package com.example.terminmenadzer.pacijenti

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.terminmenadzer.pacijenti.PacijentDao
import com.example.terminmenadzer.termini.TerminDao
import com.example.terminmenadzer.pacijenti.PacijentEntity
import com.example.terminmenadzer.termini.TerminEntity

@Database(
    entities = [PacijentEntity::class, TerminEntity::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pacijentDao(): PacijentDao
    abstract fun terminDao(): TerminDao  // <-- OVO DODAŠ OVDE!
}