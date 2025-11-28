package com.example.terminmenadzer.termini

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.terminmenadzer.R
import com.example.terminmenadzer.pacijenti.PretragaPacijenataActivity

import java.text.SimpleDateFormat
import java.util.*

class TerminiActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_termini)

        val txtDatum = findViewById<TextView>(R.id.txtDatum)
        val recyclerTermini = findViewById<RecyclerView>(R.id.recyclerTermini)
        val btnNedeljni = findViewById<Button>(R.id.btnNedeljniPregled)
        val btnMesecni = findViewById<Button>(R.id.btnMesecniPregled)

        // Prikaz današnjeg datuma
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        txtDatum.text = "Termini za danas: ${sdf.format(Date())}"

        // Primer zauzetih termina za danas (TODO: preuzmi iz baze)
        val zauzeti = listOf("09:00", "10:30", "15:15") // primer, zameni kasnije
        val terminiZaDan = generisiTermineZaDan(zauzeti)

        recyclerTermini.layoutManager = LinearLayoutManager(this)
        recyclerTermini.adapter = TerminiAdapter(terminiZaDan) { termin ->
            if (!termin.zauzet) {
                // Klik na slobodan termin: vodi na pretragu pacijenata/zakaži
                val intent = Intent(this, PretragaPacijenataActivity::class.java)
                intent.putExtra("vreme", termin.vreme)
                startActivity(intent)
            }
        }

        btnNedeljni.setOnClickListener {
            startActivity(Intent(this, NedeljniPregledActivity::class.java))
        }
        btnMesecni.setOnClickListener {
            startActivity(Intent(this, MesecniPregledActivity::class.java))
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