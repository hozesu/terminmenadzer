package com.example.terminmenadzer.pacijenti

import androidx.room.*
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PacijentDao {
    @Insert
    suspend fun insertPacijent(pacijent: PacijentEntity)
    @Insert
    suspend fun insert(pacijent: PacijentEntity)
    @Update
    suspend fun update(pacijent: PacijentEntity)
    @Delete
    suspend fun delete(pacijent: PacijentEntity)

    @Query("SELECT * FROM PacijentEntity WHERE ime LIKE :upit OR prezime LIKE :upit OR telefon LIKE :upit")
    suspend fun pretraziPacijente(upit: String): List<PacijentEntity>

    @Query("SELECT * FROM PacijentEntity")
    suspend fun sviPacijenti(): List<PacijentEntity>
    @Query("SELECT * FROM PacijentEntity ORDER BY ime ASC, prezime ASC LIMIT :limit OFFSET :offset")
    suspend fun dajPacijenteStranica(limit: Int, offset: Int): List<PacijentEntity>
    @Query("SELECT * FROM PacijentEntity WHERE id = :id")
    suspend fun dajPacijentaPoId(id: Long): PacijentEntity?

}