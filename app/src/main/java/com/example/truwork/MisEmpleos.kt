package com.example.truwork

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.truwork.adaptadores.AdapterMisEmpleos
import com.google.android.material.floatingactionbutton.FloatingActionButton

import com.google.firebase.firestore.FirebaseFirestore

class MisEmpleos : AppCompatActivity() {

    //Mostrar lista de empleos
    private val empleosList = mutableListOf<Empleos>()
    private lateinit var empleosAdapter: AdapterMisEmpleos
    private lateinit var recyclerView: RecyclerView


    private lateinit var registrarEmpleo: FloatingActionButton
    private lateinit var email: String
    private lateinit var sharedPreferences: SharedPreferences
    private val db = FirebaseFirestore.getInstance()

    private lateinit var volver: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_empleos)


        registrarEmpleo = findViewById(R.id.icon_Agregar)
        volver = findViewById(R.id.regresar)



        // Obtener SharedPreferences
        sharedPreferences = getSharedPreferences("TruSafePreferences", MODE_PRIVATE)

        // Recuperar el correo del Intent o SharedPreferences
        email = intent.getStringExtra("email") ?: sharedPreferences.getString("email", "") ?: ""



        volver.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
            finish()
        }



        recyclerView = findViewById(R.id.recyclerView2)
        empleosAdapter = AdapterMisEmpleos(empleosList)
        recyclerView.adapter = empleosAdapter

        // Configurar el LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)


        if (email.isNotEmpty()) {
            showEmailToast(email)
            // Si el correo está disponible, cargar la información
            cargarinfo()
        } else {
            Toast.makeText(this, "Correo no disponible", Toast.LENGTH_SHORT).show()
        }

        setup()

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun cargarinfo() {
        db.collection("Empleos")
            .whereEqualTo("CorreoEmpresa", email)
            .get()
            .addOnSuccessListener { documents ->
                empleosList.clear()

                for (document in documents) {
                    val id = document.id
                    val nombreCargo = document.getString("trabajo") ?: ""
                    //val nombreEmpresa = document.getString("CorreoEmpresa") ?: ""
                    val nombreCiudad = document.getString("ciudad") ?: ""
                    val nombreTiempo = document.getString("tipoEmpleo") ?: ""
                    val correoEmpresa = document.getString("CorreoEmpresa") ?: "" // Obtener el correo
                    val fechaPublicacion = document.getString("fechaPublicación") ?: ""

                    // Crear un nuevo contacto
                    val nuevoEmpleo = Empleos(id, nombreCargo,nombreCiudad, nombreTiempo, fechaPublicacion, correoEmpresa)
                    empleosList.add(nuevoEmpleo)

                    //Log.d("VerLog", "Contacto agregado: $fechaPublicacion")
                }
                // Notificar cambios al adaptador
                empleosAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al obtener empleos: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("Mis empleos", "Error al obtener Empleos: ${exception.message}")
            }
    }

    private fun setup() {
        registrarEmpleo.setOnClickListener {

            val intent = Intent(this, CargarEmpleo::class.java).apply {
                putExtra("email", email)
            }

            // Crear las opciones de actividad con las animaciones
            val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)

            // Iniciar la nueva actividad con las opciones
            startActivity(intent, options.toBundle())
        }
    }


    private fun showEmailToast(email: String?) {
        Toast.makeText(this, "El correo almacenado es: $email", Toast.LENGTH_LONG).show()
    }
}