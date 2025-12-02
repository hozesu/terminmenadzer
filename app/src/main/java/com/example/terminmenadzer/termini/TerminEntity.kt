package com.example.terminmenadzer.termini

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TerminEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val datum: String,
    val vreme: String,
    val zauzet: Boolean,
    val pacijentId: Long? = null,
    val pacijentIme: String? = null,
    val pacijentTelefon: String? = null
)