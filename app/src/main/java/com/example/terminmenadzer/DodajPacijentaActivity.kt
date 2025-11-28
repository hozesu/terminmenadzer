package com.example.terminmenadzer.pacijenti

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.terminmenadzer.R

class DodajPacijentaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dodaj_pacijenta)

        val etIme = findViewById<EditText>(R.id.etIme)
        val etPrezime = findViewById<EditText>(R.id.etPrezime)
        val etDatumRodjenja = findViewById<EditText>(R.id.etDatumRodjenja)
        val etBrojTelefona = findViewById<EditText>(R.id.etBrojTelefona)
        val btnSacuvaj = findViewById<Button>(R.id.btnSacuvajPacijenta)
        val btnNazad = findViewById<Button>(R.id.btnNazadPacijentMeni)

        btnSacuvaj.setOnClickListener {
            val ime = etIme.text.toString().trim()
            val prezime = etPrezime.text.toString().trim()
            val datumRodjenja = etDatumRodjenja.text.toString().trim()
            val brojTelefona = etBrojTelefona.text.toString().trim()

            // VALIDACIJA
            if (ime.isEmpty()) {
                etIme.error = "Unesite ime"
                etIme.requestFocus()
                return@setOnClickListener
            }
            if (prezime.isEmpty()) {
                etPrezime.error = "Unesite prezime"
                etPrezime.requestFocus()
                return@setOnClickListener
            }
            // Proveri datum rođenja
            if (!datumRodjenja.matches(Regex("""\d{2}/\d{2}/\d{4}"""))) {
                etDatumRodjenja.error = "Unesite datum u formatu dd/mm/gggg"
                etDatumRodjenja.requestFocus()
                return@setOnClickListener
            }
            // Proveri broj telefona
            if (!brojTelefona.matches(Regex("""\+\d{3} \d{2} \d{3} \d{5}"""))) {
                etBrojTelefona.error = "Unesite broj u formatu +xxx xx xxx xxxxx"
                etBrojTelefona.requestFocus()
                return@setOnClickListener
            }

            // --- Ovde dodaj upis pacijenta u bazu ---
            // npr. pozovi ViewModel ili DAO, zavisno od arhitekture

            Toast.makeText(this, "Pacijent uspešno sačuvan!", Toast.LENGTH_SHORT).show()
            finish() // Zatvori ekran i vrati se nazad
        }

        btnNazad.setOnClickListener {
            finish()
        }
    }
}