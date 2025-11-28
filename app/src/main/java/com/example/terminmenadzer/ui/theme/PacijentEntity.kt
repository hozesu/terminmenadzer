package com.example.terminmenadzer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pacijenti")
data class PacijentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ime: String,
    val prezime: String,
    val datumRodjenja: String,
    val brojTelefona: String
)