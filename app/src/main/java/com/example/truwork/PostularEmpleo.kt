package com.example.truwork

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class PostularEmpleo : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var empleoId: String
    private val db = FirebaseFirestore.getInstance()

    private lateinit var postular: MaterialButton
    private lateinit var regresar: ImageButton

    private lateinit var tituloTrabajo: TextView
    private lateinit var empresaNombre: TextView
    private lateinit var ciudadNombre: TextView
    private lateinit var tiempoNombre: TextView
    private lateinit var muestraDescripcion: TextView
    private lateinit var muestraFunciones: TextView
    private lateinit var muestraBeneficios: TextView
    private lateinit var muestraRequisitos: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postular_empleo)


        tituloTrabajo = findViewById(R.id.tituloTrabajo)
        empresaNombre = findViewById(R.id.empresaNombre)
        ciudadNombre = findViewById(R.id.ciudadNombre)
        tiempoNombre = findViewById(R.id.tiempoNombre)
        muestraDescripcion = findViewById(R.id.muestraDescripcion)
        muestraFunciones = findViewById(R.id.muestraFunciones)
        muestraBeneficios = findViewById(R.id.muestraBeneficios)
        muestraRequisitos = findViewById(R.id.muestraRequisitos)

        regresar = findViewById(R.id.btn_regresarmenu)
        postular = findViewById(R.id.btn_postular)

        regresar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
            finish()

        }

        postular.setOnClickListener {
            setup()
        }

        // Obtener SharedPreferences
        sharedPreferences = getSharedPreferences("TruSafePreferences", MODE_PRIVATE)

        // Recuperar el correo almacenado en SharedPreferences
        email = sharedPreferences.getString("email", "") ?: ""

        // Intent para obtener el email
        email = intent.getStringExtra("email") ?: ""

        // Si el correo está vacío, intenta recuperar el correo almacenado en SharedPreferences
        if (email.isEmpty()) {
            email = sharedPreferences.getString("email", "") ?: ""
        }

        Log.d("PostularEmpleo", "Email recuperado: $email")

        Toast.makeText(this, "El correo almacenado en FRAGMENT ES: $email", Toast.LENGTH_LONG).show()

        empleoId = intent.getStringExtra("idEmpleo") ?: "" // Recupera el ID del empleo




        // Mostrar Toast si el correo está disponible
        if (empleoId.isNotEmpty()) {
            cargarInfo()
            Toast.makeText(this, "el email es $email", Toast.LENGTH_SHORT).show()

        }else {

            Toast.makeText(this, "Id no disponible", Toast.LENGTH_SHORT).show()
        }



    }

    private fun cargarInfo() {
        db.collection("Empleos").document(empleoId).get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Obtener los datos
                val trabajo = document.getString("trabajo") ?: ""
                val nombreEmpresa = document.getString("nombreEmpresa") ?: ""
                val ciudad = document.getString("ciudad") ?: ""
                val tipoEmpleo = document.getString("tipoEmpleo") ?: ""
                val descripcion = document.getString("descripcion") ?: ""
                val funciones = document.getString("funciones") ?: ""
                val beneficios = document.getString("beneficios") ?: ""
                val requisitos = document.getString("requisitos") ?: ""

                tituloTrabajo.text = trabajo
                empresaNombre.text = nombreEmpresa
                ciudadNombre.text = ciudad
                tiempoNombre.text = tipoEmpleo
                muestraDescripcion.text = descripcion
                muestraFunciones.text = funciones
                muestraBeneficios.text = beneficios
                muestraRequisitos.text = requisitos

            } else {
                Log.d("Firestore", "El documento no existe")
            }
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Error al cargar los datos", e)
            Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setup() {

        val datosPostulaciones = hashMapOf(
            "empleoId" to empleoId,
            "postulanteEmail" to email
        )

        //Añadir los datos a la coleccion "Postulaciones"

        FirebaseFirestore.getInstance().collection("Postulaciones").add(datosPostulaciones)
            .addOnSuccessListener {
                Toast.makeText(this, "Postulación exitosa", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }



    }
}