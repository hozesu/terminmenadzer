package data

import android.content.Context
import androidx.room.Room
import com.example.terminmenadzer.pacijenti.AppDatabase

object DatabaseProvider {
    lateinit var db: AppDatabase
        private set

    fun init(context: Context) {
        db = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "termin_menadzer_db"
        )
            .fallbackToDestructiveMigration() // <-- OVO DODAJ OVDE
            .build()
    }
}