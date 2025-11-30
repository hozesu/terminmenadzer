package com.example.terminmenadzer.termini

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.terminmenadzer.termini.TerminEntity

@Dao
interface TerminDao {
    @Query("SELECT * FROM TerminEntity WHERE datum = :datum")
    suspend fun terminiZaDan(datum: String): List<TerminEntity>

    @Query("SELECT * FROM TerminEntity WHERE vreme = :vreme AND datum = :datum LIMIT 1")
    suspend fun nadjiTerminPoVremenu(datum: String, vreme: String): TerminEntity?

    @Update
    suspend fun update(termin: TerminEntity)

    @Insert
    suspend fun insert(termin: TerminEntity): Long

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(termini: List<TerminEntity>)
}