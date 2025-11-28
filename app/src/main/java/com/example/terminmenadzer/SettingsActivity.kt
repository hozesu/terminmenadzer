package com.example.terminmenadzer.podesavanja

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.example.terminmenadzer.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnAdminLogin = findViewById<Button>(R.id.btnAdminLogin)
        val btnBackup = findViewById<Button>(R.id.btnBackup)
        val btnRestore = findViewById<Button>(R.id.btnRestore)
        val btnShare = findViewById<Button>(R.id.btnShare)
        val btnNazad = findViewById<Button>(R.id.btnNazadGlavniMeniSettings)

        btnAdminLogin.setOnClickListener {
            // Otvori ekran za prijavu administratora
        }
        btnBackup.setOnClickListener {
            // Funkcija bekapa
        }
        btnRestore.setOnClickListener {
            // Funkcija uvoza iz bekapa
        }
        btnShare.setOnClickListener {
            // Funkcija slanja termina putem share opcije
        }
        btnNazad.setOnClickListener {
            finish()
        }
    }
}