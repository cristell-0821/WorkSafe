package com.example.truwork.adaptadores

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.truwork.EmpleosGeneral
import com.example.truwork.MisEmpleos
import com.example.truwork.PostularEmpleo
import com.example.truwork.R

class AdapterEmpleos(private var empleoList: MutableList<EmpleosGeneral>, private val email: String) : RecyclerView.Adapter<AdapterEmpleos.EmpleoViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpleoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_mostrarempleos, parent, false)
        return EmpleoViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmpleoViewHolder, position: Int) {
        val empleosgeneral = empleoList[position]
        holder.bind(empleosgeneral)

        holder.itemView.setOnClickListener {

            val intent = Intent(holder.itemView.context, PostularEmpleo::class.java).apply {
                putExtra("idEmpleo", empleosgeneral.id)
                putExtra("email", email)

                Log.d("CorreoaAdapter", "Correo recuperado: $email")

            }
            holder.itemView.context.startActivity(intent)
        }

    }


    override fun getItemCount(): Int = empleoList.size

    // Método corregido para actualizar la lista
    fun updateList(newList: List<EmpleosGeneral>) {
        Log.d("AdapterEmpleos", "Actualizando lista con ${newList.size} empleos.")
        Log.d("AdapterEmpleos", "Contenido de nueva lista: $newList") // Ver contenido
        empleoList.clear() // Limpiar la lista actual
        empleoList.addAll(newList) // Agregar los nuevos datos
        notifyDataSetChanged() // Notificar cambios al adaptador
    }






    inner class EmpleoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cargo: TextView = itemView.findViewById(R.id.nombreCargo1)
        private val empresa: TextView = itemView.findViewById(R.id.nombreEmpresa1)
        private val ciudad: TextView = itemView.findViewById(R.id.nombreCiudad1)
        private val tipo: TextView = itemView.findViewById(R.id.nombreTiempo1)
        private val fecha: TextView = itemView.findViewById(R.id.fechapublicacion1)


        fun bind(empleosgeneral: EmpleosGeneral) {
            cargo.text = empleosgeneral.cargoNombre
            empresa.text = empleosgeneral.empresaNombre
            ciudad.text = empleosgeneral.ciudadNombre
            tipo.text = empleosgeneral.tiempoNombre
            fecha.text = empleosgeneral.publicacionFecha
        }
    }


}