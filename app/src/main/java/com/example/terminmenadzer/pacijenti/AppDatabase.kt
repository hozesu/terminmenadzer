package com.example.terminmenadzer.pacijenti

import androidx.room.Database
import androidx.room.RoomDatabase
package com.example.terminmenadzer.data


@Database(
    entities = [PacijentEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pacijentDao(): PacijentDao
}