package com.example.terminmenadzer

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val edtNazivUstanove = findViewById<EditText>(R.id.edtNazivUstanove)
        val btnSacuvajNaziv = findViewById<Button>(R.id.btnSacuvajNaziv)
        val btnNapraviBackup = findViewById<Button>(R.id.btnNapraviBackup)
        val btnVratiBackup = findViewById<Button>(R.id.btnVratiBackup)

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        edtNazivUstanove.setText(prefs.getString("naziv_ustanove", ""))

        btnSacuvajNaziv.setOnClickListener {
            val naziv = edtNazivUstanove.text.toString().trim()
            prefs.edit().putString("naziv_ustanove", naziv).apply()
            Toast.makeText(this, "Naziv ustanove sačuvan!", Toast.LENGTH_SHORT).show()
        }

        btnNapraviBackup.setOnClickListener {
            // Ovdje dodaj logiku za backup
            Toast.makeText(this, "Backup funkcija nije još implementirana.", Toast.LENGTH_SHORT).show()
        }

        btnVratiBackup.setOnClickListener {
            // Ovdje dodaj logiku za vraćanje backup-a
            Toast.makeText(this, "Restore funkcija nije još implementirana.", Toast.LENGTH_SHORT).show()
        }
    }
}