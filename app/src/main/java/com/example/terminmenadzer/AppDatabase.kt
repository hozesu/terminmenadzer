package com.example.terminmenadzer.data
package com.example.terminmenadzer

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.terminmenadzer.pacijenti.PacijentEntity
import com.example.terminmenadzer.pacijenti.PacijentDao


@Database(
    entities = [PacijentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pacijentDao(): PacijentDao
}