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
    private lateinit var terminiZaDan: MutableList<Termin>
    private lateinit var adapter: TerminiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_termini)

        val txtDatum = findViewById<TextView>(R.id.txtDatum)
        val recyclerTermini = findViewById<RecyclerView>(R.id.recyclerTermini)
        val btnNedeljni = findViewById<Button>(R.id.btnNedeljniPregled)
        val btnMesecni = findViewById<Button>(R.id.btnMesecniPregled)

        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        txtDatum.text = "Termini za danas: ${sdf.format(Date())}"

        // Učitaj termine (možeš ovde kasnije iz baze)
        terminiZaDan = generisiTermineZaDan(listOf()).toMutableList()

        adapter = TerminiAdapter(terminiZaDan) { termin ->
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SELECT_PACIJENT && resultCode == Activity.RESULT_OK && data != null) {
            val pacijentId = data.getLongExtra("pacijent_id", -1)
            val vreme = izabranoVreme

            if (pacijentId != -1L && vreme != null) {
                // Pronađi pacijenta u bazi
                CoroutineScope(Dispatchers.IO).launch {
                    val pacijent = DatabaseProvider.db.pacijentDao().dajPacijentaPoId(pacijentId)
                    // Pronađi termin i podesi ga
                    withContext(Dispatchers.Main) {
                        val termin = terminiZaDan.find { it.vreme == vreme }
                        if (termin != null && pacijent != null) {
                            termin.zauzet = true
                            termin.pacijentId = pacijent.id
                            termin.pacijentIme = "${pacijent.ime} ${pacijent.prezime}"
                            termin.pacijentTelefon = pacijent.telefon
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
        }
    }

    private fun generisiTermineZaDan(zauzetiTermini: List<String>): List<Termin> {
        val termini = mutableListOf<Termin>()
        val satovi = 7..19
        val minuti = listOf(0, 15, 30, 45)
        for (sat in satovi) {
            for (minut in minuti) {
                if (sat == 19 && minut > 0) break
                val vreme = "%02d:%02d".format(sat, minut)
                val zauzet = zauzetiTermini.contains(vreme)
                termini.add(Termin(vreme, zauzet))
            }
        }
        return termini
    }
}
