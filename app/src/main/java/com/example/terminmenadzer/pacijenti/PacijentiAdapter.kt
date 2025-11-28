package com.example.terminmenadzer.pacijenti

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.terminmenadzer.R

class PacijentiAdapter(
    private var pacijenti: List<PacijentEntity>,
    private val onPacijentClick: (PacijentEntity) -> Unit
) : RecyclerView.Adapter<PacijentiAdapter.PacijentViewHolder>() {

    inner class PacijentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtImePrezime: TextView = itemView.findViewById(R.id.txtImePrezime)
        val txtBroj: TextView = itemView.findViewById(R.id.txtBroj)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacijentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pacijent, parent, false)
        return PacijentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PacijentViewHolder, position: Int) {
        val pacijent = pacijenti[position]
        holder.txtImePrezime.text = "${pacijent.ime} ${pacijent.prezime}"
        holder.txtBroj.text = pacijent.brojTelefona
        holder.itemView.setOnClickListener { onPacijentClick(pacijent) }
    }

    override fun getItemCount(): Int = pacijenti.size

    fun updateList(newList: List<PacijentEntity>) {
        pacijenti = newList
        notifyDataSetChanged()
    }
}