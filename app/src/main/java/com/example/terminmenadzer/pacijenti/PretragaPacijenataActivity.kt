package com.example.terminmenadzer.pacijenti

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.terminmenadzer.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import data.DatabaseProvider

class PretragaPacijenataActivity : AppCompatActivity() {

    private lateinit var adapter: PacijentiAdapter
    private lateinit var edtPretraga: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pretraga_pacijenata)

        edtPretraga = findViewById(R.id.edtPretraga)
        val btnPretrazi = findViewById<Button>(R.id.btnPretrazi)
        val btnNazad = findViewById<Button>(R.id.btnNazad)
        val btnNoviPacijent = findViewById<Button>(R.id.btnNoviPacijent)
        val recycler = findViewById<RecyclerView>(R.id.recyclerPacijenti)

        adapter = PacijentiAdapter(listOf()) { pacijent ->
            // Ovde dodaj akciju kada se klikne na pacijenta, npr. zakaži termin
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        btnNazad.setOnClickListener {
            finish()
        }

        btnNoviPacijent.setOnClickListener {
            val intent = Intent(this, DodajPacijentaActivity::class.java)
            startActivity(intent)
        }

        btnPretrazi.setOnClickListener {
            val upit = "%${edtPretraga.text.toString()}%"
            CoroutineScope(Dispatchers.Main).launch {
                val rezultati = DatabaseProvider.db.pacijentDao().pretraziPacijente(upit)
                adapter.updateList(rezultati)
            }
        }

        // Prikaz svih pacijenata na početku
        CoroutineScope(Dispatchers.Main).launch {
            val svi = DatabaseProvider.db.pacijentDao().sviPacijenti()
            adapter.updateList(svi)
        }
    }

    override fun onResume() {
        super.onResume()
        // Osveži listu pacijenata (da se novododati pacijent automatski vidi)
        CoroutineScope(Dispatchers.Main).launch {
            val svi = DatabaseProvider.db.pacijentDao().sviPacijenti()
            adapter.updateList(svi)
        }
    }
}