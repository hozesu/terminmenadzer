package com.example.terminmenadzer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.terminmenadzer.termini.TerminiActivity
import data.DatabaseProvider
import com.example.terminmenadzer.utils.*
import com.example.terminmenadzer.SettingsActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseProvider.init(this)
        setContentView(R.layout.activity_main)

        // Prikaz naziva ustanove na vrhu
        val tvNazivUstanove = findViewById<TextView>(R.id.tvNazivUstanove)
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val naziv = prefs.getString("naziv_ustanove", "")
        tvNazivUstanove.text = if (naziv.isNullOrEmpty()) "Naziv ustanove" else naziv

        // Dugmad za navigaciju
        val btnTermini = findViewById<Button>(R.id.btnTermini)
        val btnPacijenti = findViewById<Button>(R.id.btnPacijenti)
        val btnPodesavanja = findViewById<Button>(R.id.btnPodesavanja)

        btnTermini.setOnClickListener {
            startActivity(Intent(this, TerminiActivity::class.java))
        }

        btnPacijenti.setOnClickListener {
            startActivity(Intent(this, PacijentiActivity::class.java))
        }

        btnPodesavanja.setOnClickListener {
            if (!jeMasterPasswordPostavljen(this)) {
                prikaziDijalogZaPostavljanjeSifre(this) {
                    prikaziDijalogZaUnosSifre(this) {
                        startActivity(Intent(this, SettingsActivity::class.java))
                    }
                }
            } else {
                prikaziDijalogZaUnosSifre(this) {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
            }
        }
    }

    // Ako želiš da se naziv ustanove osveži svaki put kad se vratiš na MainActivity:
    override fun onResume() {
        super.onResume()
        val tvNazivUstanove = findViewById<TextView>(R.id.tvNazivUstanove)
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val naziv = prefs.getString("naziv_ustanove", "")
        tvNazivUstanove.text = if (naziv.isNullOrEmpty()) "Naziv ustanove" else naziv
    }
}