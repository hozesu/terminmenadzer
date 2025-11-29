package com.example.terminmenadzer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.terminmenadzer.pacijenti.PacijentiAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import data.DatabaseProvider
import android.util.Log

class PacijentiActivity : AppCompatActivity() {

    private lateinit var adapter: PacijentiAdapter
    private var trenutnaStrana = 0
    private val brojPoStrani = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pacijenti)

        val recycler = findViewById<RecyclerView>(R.id.recyclerPacijenti)
        val btnNazad = findViewById<Button>(R.id.btnNazadPacijenti)
        val btnPrethodna = findViewById<Button>(R.id.btnPrethodnaStrana)
        val btnSledeca = findViewById<Button>(R.id.btnSledecaStrana)
        val btnNoviPacijent = findViewById<Button>(R.id.btnNoviPacijent)

        // NOVO: koristi oba lambda konstruktora iz adaptera
        adapter = PacijentiAdapter(
            listOf(),
            onIzmeniClick = { pacijent ->
                val intent = Intent(this, com.example.terminmenadzer.pacijenti.IzmeniPacijentaActivity::class.java)
                intent.putExtra("id", pacijent.id)
                startActivity(intent)
            },
            onItemClick = { pacijent ->
                // Po potrebi, ovde možeš otvoriti detalje, prikazati dijalog, itd.
                // Ako ne treba ništa, ostavi prazno
            }
        )
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        btnNazad.setOnClickListener { finish() }

        btnPrethodna.setOnClickListener {
            if (trenutnaStrana > 0) {
                trenutnaStrana--
                ucitajPacijente()
            }
        }

        btnSledeca.setOnClickListener {
            trenutnaStrana++
            ucitajPacijente()
        }

        btnNoviPacijent.setOnClickListener {
            val intent = Intent(this, com.example.terminmenadzer.pacijenti.DodajPacijentaActivity::class.java)
            startActivity(intent)
        }

        ucitajPacijente()
    }

    override fun onResume() {
        super.onResume()
        ucitajPacijente()
    }

    private fun ucitajPacijente() {
        CoroutineScope(Dispatchers.Main).launch {
            val offset = trenutnaStrana * brojPoStrani
            val lista = DatabaseProvider.db.pacijentDao().dajPacijenteStranica(brojPoStrani, offset)
            Log.d("PacijentiActivity", "Učitano pacijenata: ${lista.size}")
            adapter.updateList(lista)
            // Disable/enable dugmiće...
            findViewById<Button>(R.id.btnPrethodnaStrana).isEnabled = trenutnaStrana > 0
            findViewById<Button>(R.id.btnSledecaStrana).isEnabled = lista.size == brojPoStrani
        }
    }
}