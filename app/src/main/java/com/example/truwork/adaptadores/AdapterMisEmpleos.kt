package com.example.truwork.adaptadores

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.truwork.EditarEmpleo
import com.example.truwork.Empleos
import com.example.truwork.R
import com.example.truwork.VerPostulaciones
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class AdapterMisEmpleos(private  var empleosList: MutableList<Empleos> ): RecyclerView.Adapter<AdapterMisEmpleos.EmpleosViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpleosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_layout_empleos, parent, false)
        return EmpleosViewHolder(view)
    }
    override fun onBindViewHolder(holder: EmpleosViewHolder, position: Int) {
        val misempleos = empleosList[position]
        holder.bind(misempleos)
        
        holder.itemView.setOnClickListener {
            showOptionsDialog(empleosList[position], holder.itemView)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showOptionsDialog(cargoName: Empleos, view: View) {

        // Inflar el diseño personalizado

        val dialogView = LayoutInflater.from(view.context).inflate(R.layout.dialog_custom_layout, null)
        // Crear el diálogo con el diseño personalizado
        val dialogBuilder = AlertDialog.Builder(view.context)
        dialogBuilder.setView(dialogView)

        // Obtener referencias a los botones en el diseño personalizado
        val btnEditar = dialogView.findViewById<Button>(R.id.button_edit)
        val btnEliminar = dialogView.findViewById<Button>(R.id.button_delete)
        val btnPostulaciones = dialogView.findViewById<Button>(R.id.button_view)
        val btnCerrar = dialogView.findViewById<FloatingActionButton>(R.id.icon_cerrar)

        // Crear el diálogo
        val dialog = dialogBuilder.create()


        // Configurar los clics de los botones
        btnEditar.setOnClickListener {
            val empleoId = cargoName.id // ID del documento
            val empleoEmail = cargoName.email

            Toast.makeText(view.context, "Opción Editar seleccionada para $cargoName", Toast.LENGTH_SHORT).show()
            // Lógica adicional para Editar

            val intent = Intent(view.context, EditarEmpleo::class.java).apply {
                putExtra("idEmpleo", empleoId)
                putExtra("email", empleoEmail)
            }
                view.context.startActivity(intent)

            // Cierra el diálogo después de iniciar la actividad
            dialog.dismiss()
        }

        btnEliminar.setOnClickListener {

            val database = FirebaseFirestore.getInstance()
            val empleoId = cargoName.id


            // Elimina el empleo de la base de datos
            database.collection("Empleos").document(empleoId)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(view.context, "Empleo eliminado correctamente.", Toast.LENGTH_SHORT).show()
                    empleosList.remove(cargoName)
                    notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado
                }
                .addOnFailureListener { e ->
                    Toast.makeText(view.context, "Error al eliminar el empleo: ${e.message}", Toast.LENGTH_SHORT).show()
                }

            dialog.dismiss()

            Toast.makeText(view.context, "Opción Eliminar seleccionada para $cargoName", Toast.LENGTH_SHORT).show()
            // Lógica adicional para Eliminar
            dialog.dismiss() // Opcionalmente cerrar el diálogo aquí
        }

        btnPostulaciones.setOnClickListener {

            val empleoId = cargoName.id // ID del docume

            val intent = Intent(view.context, VerPostulaciones::class.java).apply {
                putExtra("idEmpleo", empleoId)
            }
            view.context.startActivity(intent)

            // Cierra el diálogo después de iniciar la actividad
            dialog.dismiss()
        }

        btnCerrar.setOnClickListener {
            dialog.dismiss() // Cierra el diálogo
        }

        // Mostrar el diálogo
        dialog.show()
    }

    override fun getItemCount(): Int = empleosList.size



    inner class EmpleosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cargo: TextView = itemView.findViewById(R.id.cargoNombre)
        private val fecha: TextView = itemView.findViewById(R.id.fechaPublicacion)


        fun bind(empleos: Empleos) {
            cargo.text = empleos.nombreCargo
            fecha.text = empleos.fechaPublicacion

           // empresa.text = empleos.nombreEmpresa
           /* ciudad.text = empleos.nombreCiudad
            tipo.text = empleos.nombreTiempo*/
        }
    }

}