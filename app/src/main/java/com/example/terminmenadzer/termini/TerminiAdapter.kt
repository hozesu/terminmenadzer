package com.example.terminmenadzer.termini

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.terminmenadzer.R

class TerminiAdapter(
    private var termini: List<TerminEntity>,
    private val onTerminClick: (TerminEntity) -> Unit
) : RecyclerView.Adapter<TerminiAdapter.TerminViewHolder>() {

    inner class TerminViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtVreme: TextView = itemView.findViewById(R.id.txtVreme)
        val txtPacijent: TextView = itemView.findViewById(R.id.txtPacijent)
        val container: View = itemView.findViewById(R.id.terminContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TerminViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_termin, parent, false)
        return TerminViewHolder(view)
    }

    override fun onBindViewHolder(holder: TerminViewHolder, position: Int) {
        val termin = termini[position]
        holder.txtVreme.text = termin.vreme

        if (termin.zauzet) {
            holder.container.setBackgroundColor(Color.parseColor("#FFCDD2")) // svetlo crvena
            holder.txtVreme.setTextColor(Color.parseColor("#B71C1C")) // tamnocrvena
            // Prikazi ime i telefon pacijenta
            holder.txtPacijent.visibility = View.VISIBLE
            holder.txtPacijent.text = "${termin.pacijentIme ?: ""} ${termin.pacijentTelefon ?: ""}".trim()
        } else {
            holder.container.setBackgroundColor(Color.parseColor("#C8E6C9")) // svetlo zelena
            holder.txtVreme.setTextColor(Color.parseColor("#1B5E20")) // tamnozelena
            holder.txtPacijent.visibility = View.GONE
        }

        holder.container.setOnClickListener {
            onTerminClick(termin)
        }
    }

    override fun getItemCount(): Int = termini.size

    // OVO DODAJ!
    fun updateList(noviTermini: List<TerminEntity>) {
        this.termini = noviTermini
        notifyDataSetChanged()
    }
}