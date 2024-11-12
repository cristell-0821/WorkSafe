package com.example.truwork.adaptadores

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.truwork.Postulaciones
import com.example.truwork.R
import com.example.truwork.VerPostulaciones
import com.google.firebase.storage.FirebaseStorage
import java.io.File

// AdapterPostulaciones.kt
class AdapterPostulaciones(
    private var postulacionesList: MutableList<Postulaciones>,
    private val context: Context  // Agrega el contexto aquí
) : RecyclerView.Adapter<AdapterPostulaciones.PostulacionesViewHolder>() {

    private val storage = FirebaseStorage.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostulacionesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_postulaciones, parent, false)
        return PostulacionesViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostulacionesViewHolder, position: Int) {
        val postulaciones = postulacionesList[position]
        holder.bind(postulaciones)
    }

    override fun getItemCount(): Int = postulacionesList.size

    inner class PostulacionesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nombrePostulantes: TextView = itemView.findViewById(R.id.NombrePostulantes)
        private val correoPostulante: TextView = itemView.findViewById(R.id.correoPostulantes)
        private val telefonoPostulante: TextView = itemView.findViewById(R.id.telefonoPostulantes)
        private val descargarCvButton: Button = itemView.findViewById(R.id.btn_descargar)

        fun bind(postulaciones: Postulaciones) {
            nombrePostulantes.text = postulaciones.nombrePostulante
            correoPostulante.text = postulaciones.correoPostulante
            telefonoPostulante.text = postulaciones.telefonoPostulante

            descargarCvButton.setOnClickListener {
                (context as? VerPostulaciones)?.descargarCV(postulaciones.correoPostulante)  // Llama a descargarCV en VerPostulaciones
            }
        }
    }
}

