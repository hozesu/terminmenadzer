package com.example.terminmenadzer.termini

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
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

class TerminiActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_SELECT_PACIJENT = 1001
    }

    private var izabranoVreme: String? = null
    private lateinit var adapter: TerminiAdapter
    private val datum: String by lazy {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        sdf.format(Date())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_termini)

        val txtDatum = findViewById<TextView>(R.id.txtDatum)
        val recyclerTermini = findViewById<RecyclerView>(R.id.recyclerTermini)
        val btnNedeljni = findViewById<Button>(R.id.btnNedeljniPregled)
        val btnMesecni = findViewById<Button>(R.id.btnMesecniPregled)

        txtDatum.text = "Termini za danas: $datum"

        // Adapter inicijalno prazan, puniće se iz baze
        adapter = TerminiAdapter(mutableListOf()) { termin ->
            if (!termin.zauzet) {
                izabranoVreme = termin.vreme
                val intent = Intent(this, PretragaPacijenataActivity::class.java)
                startActivityForResult(intent, REQUEST_SELECT_PACIJENT)
            }
        }
        recyclerTermini.layoutManager = LinearLayoutManager(this)
        recyclerTermini.adapter = adapter

        btnNedeljni.setOnClickListener {
            startActivity(Intent(this, NedeljniPregledActivity::class.java))
        }
        btnMesecni.setOnClickListener {
            startActivity(Intent(this, MesecniPregledActivity::class.java))
        }

        // Učitaj termine iz baze
        ucitajTermine()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_PACIJENT && resultCode == Activity.RESULT_OK && data != null) {
            val pacijentId = data.getLongExtra("pacijent_id", -1)
            val vreme = izabranoVreme

            if (pacijentId != -1L && vreme != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val terminEntity = DatabaseProvider.db.terminDao().nadjiTerminPoVremenu(datum, vreme)
                    if (terminEntity != null) {
                        val noviTermin = terminEntity.copy(zauzet = true, pacijentId = pacijentId)
                        DatabaseProvider.db.terminDao().update(noviTermin)
                    }
                    withContext(Dispatchers.Main) {
                        ucitajTermine()
                    }
                }
            }
        }
    }

    // Učitava sve termine za danas iz baze, puni adapter
    private fun ucitajTermine() {
        CoroutineScope(Dispatchers.IO).launch {
            var termini = DatabaseProvider.db.terminDao().terminiZaDan(datum)
            // Ako nema termina za danas, generiši ih i upiši u bazu
            if (termini.isEmpty()) {
                val noviTermini = generisiTermineZaDan().map {
                    TerminEntity(datum = datum, vreme = it, zauzet = false)
                }
                DatabaseProvider.db.terminDao().insertAll(noviTermini)
                termini = DatabaseProvider.db.terminDao().terminiZaDan(datum)
            }

            // Poveži podatke o pacijentu za svaki termin
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
                Termin(
                    vreme = termin.vreme,
                    zauzet = termin.zauzet,
                    pacijentId = termin.pacijentId,
                    pacijentIme = ime,
                    pacijentTelefon = telefon
                )
            }
            withContext(Dispatchers.Main) {
                adapter.updateList(prikazTermini)
            }
        }
    }

    // Generiše sva vremena termina za dan (format HH:mm)
    private fun generisiTermineZaDan(): List<String> {
        val satovi = 7..19
        val minuti = listOf(0, 15, 30, 45)
        val lista = mutableListOf<String>()
        for (sat in satovi) {
            for (minut in minuti) {
                if (sat == 19 && minut > 0) break
                lista.add("%02d:%02d".format(sat, minut))
            }
        }
        return lista
    }
}

