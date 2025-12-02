package com.example.terminmenadzer.termini

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.terminmenadzer.R
import com.example.terminmenadzer.pacijenti.PretragaPacijenataActivity
import data.DatabaseProvider
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.appcompat.app.AlertDialog

class TerminiActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_SELECT_PACIJENT = 1001
    }

    private var izabranoVreme: String? = null
    private lateinit var adapter: TerminiAdapter

    private var izabraniDatum: String = getDanasnjiDatum()

    private lateinit var txtDatum: TextView
    private lateinit var txtAktuelniDatum: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_termini)

        txtAktuelniDatum = findViewById(R.id.txtAktuelniDatum)
        txtDatum = findViewById(R.id.txtDatum)
        val btnKalendar = findViewById<Button>(R.id.btnKalendar)
        val recyclerTermini = findViewById<RecyclerView>(R.id.recyclerTermini)

        val danasnjiDatum = getDanasnjiDatum()
        txtAktuelniDatum.text = "Aktuelni datum: $danasnjiDatum"
        // Izgleda kao link
        txtAktuelniDatum.paintFlags = txtAktuelniDatum.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        txtAktuelniDatum.setTextColor(resources.getColor(R.color.purple_700, theme))
        txtAktuelniDatum.isClickable = true

        txtAktuelniDatum.setOnClickListener {
            izabraniDatum = danasnjiDatum
            txtDatum.text = "Termini za dan: $izabraniDatum"
            ucitajTermine()
        }

        txtDatum.text = "Termini za dan: $izabraniDatum"

        btnKalendar.setOnClickListener {
            prikaziKalendar()
        }

        adapter = TerminiAdapter(mutableListOf()) { termin ->
            if (termin.zauzet) {
                handleIzmeni(termin)
            } else {
                izabranoVreme = termin.vreme
                val intent = Intent(this, PretragaPacijenataActivity::class.java)
                startActivityForResult(intent, REQUEST_SELECT_PACIJENT)
            }
        }
        recyclerTermini.layoutManager = LinearLayoutManager(this)
        recyclerTermini.adapter = adapter

        ucitajTermine()
    }

    private fun prikaziKalendar() {
        val cal = Calendar.getInstance()
        try {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            val date = sdf.parse(izabraniDatum)
            if (date != null) cal.time = date
        } catch (_: Exception) {}

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            val noviDatum = String.format("%02d.%02d.%04d", d, m + 1, y)
            izabraniDatum = noviDatum
            txtDatum.text = "Termini za dan: $izabraniDatum"
            ucitajTermine()
        }, year, month, day).show()
    }

    private fun getDanasnjiDatum(): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_PACIJENT && resultCode == Activity.RESULT_OK && data != null) {
            val pacijentId = data.getLongExtra("pacijent_id", -1)
            val pacijentIme = data.getStringExtra("pacijent_ime")
            val pacijentTelefon = data.getStringExtra("pacijent_telefon")
            val vreme = izabranoVreme

            if (pacijentId != -1L && vreme != null) {
                val komentarEditText = EditText(this)
                komentarEditText.hint = "Komentar za termin (opciono)"

                AlertDialog.Builder(this)
                    .setTitle("Dodaj komentar")
                    .setView(komentarEditText)
                    .setPositiveButton("Sačuvaj") { _, _ ->
                        val komentar = komentarEditText.text.toString()
                        CoroutineScope(Dispatchers.IO).launch {
                            val terminEntity = DatabaseProvider.db.terminDao().nadjiTerminPoVremenu(izabraniDatum, vreme)
                            if (terminEntity != null) {
                                val noviTermin = terminEntity.copy(
                                    zauzet = true,
                                    pacijentId = pacijentId,
                                    pacijentIme = pacijentIme,
                                    pacijentTelefon = pacijentTelefon,
                                    komentar = if (komentar.isNotBlank()) komentar else null
                                )
                                DatabaseProvider.db.terminDao().update(noviTermin)
                            }
                            withContext(Dispatchers.Main) {
                                ucitajTermine()
                            }
                        }
                    }
                    .setNegativeButton("Preskoči") { _, _ ->
                        CoroutineScope(Dispatchers.IO).launch {
                            val terminEntity = DatabaseProvider.db.terminDao().nadjiTerminPoVremenu(izabraniDatum, vreme)
                            if (terminEntity != null) {
                                val noviTermin = terminEntity.copy(
                                    zauzet = true,
                                    pacijentId = pacijentId,
                                    pacijentIme = pacijentIme,
                                    pacijentTelefon = pacijentTelefon,
                                    komentar = null
                                )
                                DatabaseProvider.db.terminDao().update(noviTermin)
                            }
                            withContext(Dispatchers.Main) {
                                ucitajTermine()
                            }
                        }
                    }
                    .show()
            }
        }
    }

    private fun ucitajTermine() {
        CoroutineScope(Dispatchers.IO).launch {
            var termini = DatabaseProvider.db.terminDao().terminiZaDan(izabraniDatum)
            if (termini.isEmpty()) {
                val noviTermini = generisiTermineZaDan().map {
                    TerminEntity(datum = izabraniDatum, vreme = it, zauzet = false)
                }
                DatabaseProvider.db.terminDao().insertAll(noviTermini)
                termini = DatabaseProvider.db.terminDao().terminiZaDan(izabraniDatum)
            }

            val prikazTermini = termini.map { termin ->
                var ime = ""
                var telefon = ""
                if (termin.pacijentId != null) {
                    val pacijent = DatabaseProvider.db.pacijentDao().dajPacijentaPoId(termin.pacijentId)
                    if (pacijent != null) {
                        ime = "${pacijent.ime} ${pacijent.prezime}"
                        telefon = pacijent.telefon
                    }
                }
                TerminEntity(
                    datum = termin.datum,
                    vreme = termin.vreme,
                    zauzet = termin.zauzet,
                    pacijentId = termin.pacijentId,
                    pacijentIme = ime,
                    pacijentTelefon = telefon,
                    komentar = termin.komentar
                )
            }
            withContext(Dispatchers.Main) {
                adapter.updateList(prikazTermini)
            }
        }
    }

    private fun generisiTermineZaDan(): List<String> {
        val lista = mutableListOf<String>()
        val satovi = 8..17
        val minuti = listOf(0, 30)
        for (sat in satovi) {
            for (minut in minuti) {
                if (sat == 17 && minut > 0) break
                lista.add("%02d:%02d".format(sat, minut))
            }
        }
        return lista
    }

    private fun handleIzmeni(termin: TerminEntity) {
        showIzmeniDialog(termin)
    }

    private fun showIzmeniDialog(termin: TerminEntity) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_izmeni_termin, null)
        val txtPacijent = dialogView.findViewById<TextView>(R.id.txtTrenutniPacijent)
        val btnZameni = dialogView.findViewById<Button>(R.id.btnZameniPacijenta)
        val btnOslobodi = dialogView.findViewById<Button>(R.id.btnOslobodi)
        val etKomentar = dialogView.findViewById<EditText>(R.id.etKomentar)
        val btnSacuvajKomentar = dialogView.findViewById<Button>(R.id.btnSacuvajKomentar)
        val btnObrisiKomentar = dialogView.findViewById<Button>(R.id.btnObrisiKomentar)

        txtPacijent.text = "Pacijent: ${termin.pacijentIme ?: ""}\nTelefon: ${termin.pacijentTelefon ?: ""}"

        etKomentar?.setText(termin.komentar ?: "")

        val dialog = AlertDialog.Builder(this)
            .setTitle("Izmeni termin")
            .setView(dialogView)
            .create()

        btnZameni.setOnClickListener {
            izabranoVreme = termin.vreme
            dialog.dismiss()
            val intent = Intent(this, PretragaPacijenataActivity::class.java)
            startActivityForResult(intent, REQUEST_SELECT_PACIJENT)
        }
        btnOslobodi.setOnClickListener {
            dialog.dismiss()
            oslobodiTermin(termin)
        }

        btnSacuvajKomentar.setOnClickListener {
            val noviKomentar = etKomentar.text.toString()
            CoroutineScope(Dispatchers.IO).launch {
                val ent = DatabaseProvider.db.terminDao().nadjiTerminPoVremenu(izabraniDatum, termin.vreme)
                if (ent != null) {
                    val novi = ent.copy(komentar = if (noviKomentar.isNotBlank()) noviKomentar else null)
                    DatabaseProvider.db.terminDao().update(novi)
                }
                withContext(Dispatchers.Main) {
                    ucitajTermine()
                }
            }
            dialog.dismiss()
        }
        btnObrisiKomentar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val ent = DatabaseProvider.db.terminDao().nadjiTerminPoVremenu(izabraniDatum, termin.vreme)
                if (ent != null) {
                    val novi = ent.copy(komentar = null)
                    DatabaseProvider.db.terminDao().update(novi)
                }
                withContext(Dispatchers.Main) {
                    ucitajTermine()
                }
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun oslobodiTermin(termin: TerminEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            val ent = DatabaseProvider.db.terminDao().nadjiTerminPoVremenu(izabraniDatum, termin.vreme)
            if (ent != null) {
                val novi = ent.copy(zauzet = false, pacijentId = null, pacijentIme = null, pacijentTelefon = null, komentar = null)
                DatabaseProvider.db.terminDao().update(novi)
            }
            withContext(Dispatchers.Main) {
                ucitajTermine()
            }
        }
    }
}