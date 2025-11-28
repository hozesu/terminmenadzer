package com.example.terminmenadzer.pacijenti

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PacijentDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(pacijent: PacijentEntity): Long

    @Update
    suspend fun update(pacijent: PacijentEntity)

    @Query("SELECT * FROM pacijenti WHERE id = :id")
    suspend fun getById(id: Int): PacijentEntity?

    @Query("SELECT * FROM pacijenti WHERE ime LIKE :ime OR brojTelefona LIKE :brojTelefona")
    suspend fun search(ime: String, brojTelefona: String): List<PacijentEntity>

    @Query("SELECT * FROM pacijenti")
    suspend fun getAll(): List<PacijentEntity>

    @Query("SELECT * FROM pacijenti WHERE ime LIKE :query OR prezime LIKE :query")
    suspend fun pretraziPacijente(query: String): List<PacijentEntity>

    @Query("SELECT * FROM pacijenti")
    suspend fun sviPacijenti(): List<PacijentEntity>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun ubaciPacijenta(pacijent: PacijentEntity)
}