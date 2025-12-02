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
import androidx.appcompat.app.AlertDialog

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

        btnNedeljni.setOnClickListener {
            startActivity(Intent(this, NedeljniPregledActivity::class.java))
        }
        btnMesecni.setOnClickListener {
            startActivity(Intent(this, MesecniPregledActivity::class.java))
        }

        ucitajTermine()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_PACIJENT && resultCode == Activity.RESULT_OK && data != null) {
            val pacijentId = data.getLongExtra("pacijent_id", -1)
            val pacijentIme = data.getStringExtra("pacijent_ime")
            val pacijentTelefon = data.getStringExtra("pacijent_telefon")
            val vreme = izabranoVreme

            if (pacijentId != -1L && vreme != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val terminEntity = DatabaseProvider.db.terminDao().nadjiTerminPoVremenu(datum, vreme)
                    if (terminEntity != null) {
                        val noviTermin = terminEntity.copy(
                            zauzet = true,
                            pacijentId = pacijentId,
                            pacijentIme = pacijentIme,
                            pacijentTelefon = pacijentTelefon
                        )
                        DatabaseProvider.db.terminDao().update(noviTermin)
                    }
                    withContext(Dispatchers.Main) {
                        ucitajTermine()
                    }
                }
            }
        }
    }

    private fun ucitajTermine() {
        CoroutineScope(Dispatchers.IO).launch {
            var termini = DatabaseProvider.db.terminDao().terminiZaDan(datum)
            if (termini.isEmpty()) {
                val noviTermini = generisiTermineZaDan().map {
                    TerminEntity(datum = datum, vreme = it, zauzet = false)
                }
                DatabaseProvider.db.terminDao().insertAll(noviTermini)
                termini = DatabaseProvider.db.terminDao().terminiZaDan(datum)
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
                    datum = termin.datum,               // <-- OVO DODAJ
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

    private fun generisiTermineZaDan(): List<String> {
        val lista = mutableListOf<String>()
        val satovi = 8..17 // od 08 do 17
        val minuti = listOf(0, 30) // na svakih 30 minuta

        for (sat in satovi) {
            for (minut in minuti) {
                // poslednji termin u 17:00, IGNORIŠE 17:30
                if (sat == 17 && minut > 0) break
                lista.add("%02d:%02d".format(sat, minut))
            }
        }
        return lista
    }

    private fun handleIzmeni(termin: TerminEntity  ) {
        showIzmeniDialog(termin)
    }

    private fun showIzmeniDialog(termin: TerminEntity) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_izmeni_termin, null)
        val txtPacijent = dialogView.findViewById<TextView>(R.id.txtTrenutniPacijent)
        val btnZameni = dialogView.findViewById<Button>(R.id.btnZameniPacijenta)
        val btnOslobodi = dialogView.findViewById<Button>(R.id.btnOslobodi)

        txtPacijent.text = "Pacijent: ${termin.pacijentIme ?: ""}\nTelefon: ${termin.pacijentTelefon ?: ""}"

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

        dialog.show()
    }

    private fun oslobodiTermin(termin: TerminEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            val ent = DatabaseProvider.db.terminDao().nadjiTerminPoVremenu(datum, termin.vreme)
            if (ent != null) {
                val novi = ent.copy(zauzet = false, pacijentId = null, pacijentIme = null, pacijentTelefon = null)
                DatabaseProvider.db.terminDao().update(novi)
            }
            withContext(Dispatchers.Main) {
                ucitajTermine()
            }
        }
    }
}