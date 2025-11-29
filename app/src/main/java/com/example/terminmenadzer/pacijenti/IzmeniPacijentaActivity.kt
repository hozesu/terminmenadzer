package com.example.terminmenadzer.pacijenti

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import data.DatabaseProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.terminmenadzer.R

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
        Log.d("IzmeniPacijenta", "Primljen id: $pacijentId")

        if (pacijentId == -1L) {
            Log.e("IzmeniPacijenta", "Greska: ID nije prosleđen!")
            Toast.makeText(this, "Greška: Nepoznat pacijent!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Učitaj podatke pacijenta i popuni polja
        CoroutineScope(Dispatchers.Main).launch {
            Log.d("IzmeniPacijenta", "Pokušavam da dobijem pacijenta iz baze za id=$pacijentId")
            val pacijent = DatabaseProvider.db.pacijentDao().dajPacijentaPoId(pacijentId)
            Log.d("IzmeniPacijenta", "Pacijent iz baze: $pacijent")
            if (pacijent == null) {
                Log.e("IzmeniPacijenta", "Pacijent nije pronađen u bazi!")
                Toast.makeText(this@IzmeniPacijentaActivity, "Pacijent nije pronađen!", Toast.LENGTH_SHORT).show()
                finish()
                return@launch
            }
            etIme.setText(pacijent.ime)
            etPrezime.setText(pacijent.prezime)
            etDatumRodjenja.setText(pacijent.datumRodjenja)
            etTelefon.setText(pacijent.telefon)
        }

        btnSacuvajIzmene.setOnClickListener {
            val ime = etIme.text.toString().trim()
            val prezime = etPrezime.text.toString().trim()
            val datumRodjenja = etDatumRodjenja.text.toString().trim()
            val telefon = etTelefon.text.toString().trim()

            Log.d("IzmeniPacijenta", "Klik na Sačuvaj izmene. Unos: $ime $prezime $datumRodjenja $telefon")

            if (ime.isEmpty() || prezime.isEmpty() || datumRodjenja.isEmpty() || telefon.isEmpty()) {
                Log.e("IzmeniPacijenta", "Neka polja su prazna!")
                Toast.makeText(this, "Popunite sva polja!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update pacijenta u bazi
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d("IzmeniPacijenta", "Pokušavam da ažuriram pacijenta u bazi...")
                    val updatedRows = DatabaseProvider.db.pacijentDao().update(
                        PacijentEntity(
                            id = pacijentId,
                            ime = ime,
                            prezime = prezime,
                            datumRodjenja = datumRodjenja,
                            telefon = telefon
                        )
                    )
                    Log.d("IzmeniPacijenta", "Broj ažuriranih redova: $updatedRows")
                    runOnUiThread {
                        Toast.makeText(this@IzmeniPacijentaActivity, "Pacijent uspešno izmenjen!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } catch (e: Exception) {
                    Log.e("IzmeniPacijenta", "Greška pri ažuriranju: ${e.message}", e)
                    runOnUiThread {
                        Toast.makeText(this@IzmeniPacijentaActivity, "Greška pri izmeni!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}