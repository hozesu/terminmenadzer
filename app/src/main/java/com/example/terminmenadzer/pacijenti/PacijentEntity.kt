package com.example.terminmenadzer.pacijenti

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PacijentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val ime: String,
    val prezime: String,
    val datumRodjenja: String,
    val telefon: String
)