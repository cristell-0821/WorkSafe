package com.example.truwork.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.truwork.Fav
import com.example.truwork.R

class AdapterFav(private var favList: MutableList<Fav>) : RecyclerView.Adapter<AdapterFav.FavViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_fav, parent, false)
        return FavViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val misfav = favList[position]
        holder.bind(misfav)
    }

    override fun getItemCount(): Int = favList.size

    inner class FavViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cargo: TextView = itemView.findViewById(R.id.nombreCargo11)
        private val empresa: TextView = itemView.findViewById(R.id.nombreEmpresa12)
        private val ciudad: TextView = itemView.findViewById(R.id.nombreCiudad13)
        private val tipo: TextView = itemView.findViewById(R.id.nombreTiempo14)
        private val fecha: TextView = itemView.findViewById(R.id.fechapublicacion15)

        fun bind(fav: Fav) {
            // Manejo de datos nulos o vacíos
            cargo.text = fav.cargoNombre.ifEmpty { "No disponible" }
            empresa.text = fav.empresaNombre.ifEmpty { "No disponible" }
            ciudad.text = fav.ciudadNombre.ifEmpty { "No disponible" }
            tipo.text = fav.tiempoNombre.ifEmpty { "No disponible" }
            fecha.text = fav.publicacionFecha.ifEmpty { "Fecha no disponible" }
        }
    }
}
