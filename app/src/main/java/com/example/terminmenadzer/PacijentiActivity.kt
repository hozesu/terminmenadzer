package com.example.terminmenadzer.pacijenti

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.example.terminmenadzer.R

class PacijentiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pacijenti)

        val btnListaPacijenata = findViewById<Button>(R.id.btnListaPacijenata)
        val btnPretragaPacijenata = findViewById<Button>(R.id.btnPretragaPacijenataPacijenti)
        val btnDodajPacijenta = findViewById<Button>(R.id.btnDodajPacijenta)
        val btnNazad = findViewById<Button>(R.id.btnNazadGlavniMeniPacijenti)

        btnListaPacijenata.setOnClickListener {
            // Otvori listu pacijenata
        }
        btnPretragaPacijenata.setOnClickListener {
            // Funkcionalnost pretrage
        }
        btnDodajPacijenta.setOnClickListener {
            // Otvori formu za dodavanje novog pacijenta
        }
        btnNazad.setOnClickListener {
            finish()
        }
    }
}