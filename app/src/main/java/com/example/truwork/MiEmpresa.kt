package com.example.truwork

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore


class MiEmpresa : AppCompatActivity() {


    private lateinit var email: String
    private lateinit var sharedPreferences: SharedPreferences
    private val db = FirebaseFirestore.getInstance()

    private lateinit var regresar: ImageButton
    private lateinit var rucempresa: EditText
    private lateinit var nombreempresa: EditText
    private lateinit var presentacionempresa: EditText
    private lateinit var guardar: MaterialButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_mi_empresa)

        sharedPreferences = getSharedPreferences("TruSafePreferences", MODE_PRIVATE)
        // Recuperar el correo almacenado en SharedPreferences
        email = sharedPreferences.getString("email", "") ?: ""
        // Primero, intentamos obtener el correo del Intent
        email = intent.getStringExtra("email") ?: ""

        // Si el correo está vacío, intenta recuperar el correo almacenado en SharedPreferences
        if (email.isEmpty()) {
            email = sharedPreferences.getString("email", "") ?: ""
        }

        // Mostrar el correo en un Toast
        showEmailToast(email)




        regresar = findViewById(R.id.btn_regresarmenu)
        rucempresa = findViewById(R.id.ruc_empresa)
        nombreempresa = findViewById(R.id.nombre_empresa)
        presentacionempresa = findViewById(R.id.presentacion_empresa)
        guardar = findViewById(R.id.btn_guardar)


        regresar.setOnClickListener {

            finish()

        }

        guardar.setOnClickListener {
            setup()
        }


        if (email.isNotEmpty()) {
            showEmailToast(email)
            // Si el correo está disponible, cargar la información
            cargarinfo()
        } else {
            Toast.makeText(this, "Correo no disponible", Toast.LENGTH_SHORT).show()
        }





    }

    private fun cargarinfo() {
        // Consulta la base de datos usando el correo proporcionado
        db.collection("Empresa").document(email).get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Obtener los datos
                val nom = document.getString("nombre") ?: ""
                val rucempre = document.getString("ruc") ?: ""
                val descr = document.getString("presentacion") ?: ""

                rucempresa.setText(rucempre)
                nombreempresa.setText(nom)
                presentacionempresa.setText(descr)

            } else {
                Log.d("Firestore", "El documento no existe")
            }
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Error al cargar los datos", e)
            Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setup() {
        val ruc: String = rucempresa.text.toString()
        val nombreempresa: String = nombreempresa.text.toString()
        val presentacionempresa: String = presentacionempresa.text.toString()

        val empresaData = hashMapOf(
            "ruc" to ruc,
            "nombre" to nombreempresa,
            "presentacion" to presentacionempresa
        )

        // Añadimos los datos a la colección 'Empresa', identificando por el correo
        FirebaseFirestore.getInstance().collection("Empresa")
            .document(email)
            .set(empresaData)
            .addOnSuccessListener {
                Toast.makeText(this, "Datos de la empresa guardados", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun showEmailToast(email: String?) {
        Toast.makeText(this, "El correo almacenado es: $email", Toast.LENGTH_LONG).show()
    }

}