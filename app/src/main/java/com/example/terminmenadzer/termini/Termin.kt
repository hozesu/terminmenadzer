package com.example.terminmenadzer.termini

data class Termin(
    val vreme: String,
    var zauzet: Boolean,
    var pacijentId: Long? = null,
    var pacijentIme: String? = null,
    var pacijentTelefon: String? = null
)