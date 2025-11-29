package com.example.terminmenadzer.pacijenti

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.example.terminmenadzer.R

class PacijentiAdapter(
    private var pacijenti: List<PacijentEntity>,
    private val onIzmeniClick: (PacijentEntity) -> Unit
) : RecyclerView.Adapter<PacijentiAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvImePrezime = itemView.findViewById<TextView>(R.id.tvImePrezime)
        val tvDatumRodjenja = itemView.findViewById<TextView>(R.id.tvDatumRodjenja)
        val tvTelefon = itemView.findViewById<TextView>(R.id.tvTelefon)
        val btnIzmeniPacijenta = itemView.findViewById<Button>(R.id.btnIzmeniPacijenta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pacijent, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = pacijenti.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pacijent = pacijenti[position]
        holder.tvImePrezime.text = "${pacijent.ime} ${pacijent.prezime}"
        holder.tvDatumRodjenja.text = pacijent.datumRodjenja
        holder.tvTelefon.text = pacijent.telefon

        holder.btnIzmeniPacijenta.setOnClickListener {
            onIzmeniClick(pacijent)
        }
    }

    fun updateList(novaLista: List<PacijentEntity>) {
        this.pacijenti = novaLista
        notifyDataSetChanged()
    }
}