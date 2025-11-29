package com.example.terminmenadzer.pacijenti

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import data.DatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IzmeniPacijentaActivity : AppCompatActivity() {

    private var pacijentId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_izmeni_pacijenta)

        val etIme = findViewById<EditText>(R.id.etIme)
        val etPrezime = findViewById<EditText>(R.id.etPrezime)
        val etDatumRodjenja = findViewById<EditText>(R.id.etDatumRodjenja)
        val etTelefon = findViewById<EditText>(R.id.etTelefon)
        val btnSacuvajIzmene = findViewById<Button>(R.id.btnSacuvajIzmene)

        pacijentId = intent.getLongExtra("id", -1)

        // Učitaj podatke pacijenta i popuni polja
        CoroutineScope(Dispatchers.Main).launch {
            val pacijent = DatabaseProvider.db.pacijentDao().dajPacijentaPoId(pacijentId)
            pacijent?.let {
                etIme.setText(it.ime)
                etPrezime.setText(it.prezime)
                etDatumRodjenja.setText(it.datumRodjenja)
                etTelefon.setText(it.telefon)
            }
        }

        btnSacuvajIzmene.setOnClickListener {
            val ime = etIme.text.toString().trim()
            val prezime = etPrezime.text.toString().trim()
            val datumRodjenja = etDatumRodjenja.text.toString().trim()
            val telefon = etTelefon.text.toString().trim()

            if (ime.isEmpty() || prezime.isEmpty() || datumRodjenja.isEmpty() || telefon.isEmpty()) {
                Toast.makeText(this, "Popunite sva polja!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update pacijenta u bazi
            CoroutineScope(Dispatchers.IO).launch {
                DatabaseProvider.db.pacijentDao().update(
                    PacijentEntity(
                        id = pacijentId,
                        ime = ime,
                        prezime = prezime,
                        datumRodjenja = datumRodjenja,
                        telefon = telefon
                    )
                )
                runOnUiThread {
                    Toast.makeText(this@IzmeniPacijentaActivity, "Pacijent uspešno izmenjen!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}