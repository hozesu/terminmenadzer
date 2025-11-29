package com.example.terminmenadzer.pacijenti

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.terminmenadzer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import data.DatabaseProvider

class DodajPacijentaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dodaj_pacijenta)

        val etIme = findViewById<EditText>(R.id.etIme)
        val etPrezime = findViewById<EditText>(R.id.etPrezime)
        val etDatumRodjenja = findViewById<EditText>(R.id.etDatumRodjenja)
        etDatumRodjenja.addTextChangedListener(object : android.text.TextWatcher {
            private var isFormatting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: android.text.Editable?) {
                if (isFormatting) return
                isFormatting = true

                val input = s.toString().replace("/", "")
                val builder = StringBuilder()

                var i = 0
                while (i < input.length && i < 8) {
                    builder.append(input[i])
                    if ((i == 1 || i == 3) && i != input.length - 1) {
                        builder.append("/")
                    }
                    i++
                }

                val formatted = builder.toString()
                if (formatted != s.toString()) {
                    etDatumRodjenja.setText(formatted)
                    etDatumRodjenja.setSelection(formatted.length)
                }

                isFormatting = false
            }
        })
        val etBrojTelefona = findViewById<EditText>(R.id.etBrojTelefona)
        etBrojTelefona.addTextChangedListener (object : android.text.TextWatcher {
            private var isFormatting = false
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: android.text.Editable?) {
                if (isFormatting) return
                isFormatting = true

                val input = s.toString().replace(" ", "")
                val builder = StringBuilder()

                var i = 0
                while (i < input.length && i < 14) {
                    // Dodaj +
                    if (i == 0 && input[0] != '+') {
                        builder.append("+")
                    }
                    builder.append(input[i])
                    // Format: +xxx xx xxx xxxx
                    if (i == 3 || i == 5 || i == 8) {
                        if (i != input.length - 1) {
                            builder.append(" ")
                        }
                    }
                    i++
                }

                val formatted = builder.toString()
                if (formatted != s.toString()) {
                    etBrojTelefona.setText(formatted)
                    etBrojTelefona.setSelection(formatted.length)
                }

                isFormatting = false
            }
        })
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
            if (!datumRodjenja.matches(Regex("""\d{2}/\d{2}/\d{4}"""))) {
                etDatumRodjenja.error = "Unesite datum u formatu dd/mm/gggg"
                etDatumRodjenja.requestFocus()
                return@setOnClickListener
            }
            if (!brojTelefona.matches(Regex("""\+\d{3} \d{2} \d{3} \d{4}"""))) {
                etBrojTelefona.error = "Unesite broj u formatu +xxx xx xxx xxxx"
                etBrojTelefona.requestFocus()
                return@setOnClickListener
            }

            // UPIS U BAZU
            CoroutineScope(Dispatchers.IO).launch {
                DatabaseProvider.db.pacijentDao().insert(
                    PacijentEntity(
                        ime = ime,
                        prezime = prezime,
                        datumRodjenja = datumRodjenja,
                        telefon = brojTelefona
                    )
                )
                // Vraćanje na UI thread za Toast i finish
                runOnUiThread {
                    Toast.makeText(this@DodajPacijentaActivity, "Pacijent uspešno sačuvan!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        btnNazad.setOnClickListener {
            finish()
        }
    }
}