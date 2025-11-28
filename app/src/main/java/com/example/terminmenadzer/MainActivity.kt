package com.example.terminmenadzer

import com.example.terminmenadzer.termini.TerminiActivity
import com.example.terminmenadzer.pacijenti.PacijentiActivity
import com.example.terminmenadzer.podesavanja.SettingsActivity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseProvider.init(applicationContext)
        setContentView(R.layout.activity_main)

        // Dugmad za navigaciju
        val btnTermini = findViewById<Button>(R.id.btnTermini)
        val btnPacijenti = findViewById<Button>(R.id.btnPacijenti)
        val btnPodesavanja = findViewById<Button>(R.id.btnPodesavanja)

        btnTermini.setOnClickListener {
            // Otvori Termini ekran
            startActivity(Intent(this, TerminiActivity::class.java))
        }

        btnPacijenti.setOnClickListener {
            // Otvori Pacijenti ekran
            startActivity(Intent(this, PacijentiActivity::class.java))
        }

        btnPodesavanja.setOnClickListener {
            // Otvori Podešavanja ekran
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}