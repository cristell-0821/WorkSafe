package com.example.truwork

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class EditarEmpleo : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var email: String
    private lateinit var idEmpleo: String

    //Obtener nombre de la empresa
    private lateinit var nombreEmpresa: String
    private lateinit var fechaEmpleo: String

    private lateinit var regresar: ImageView
    private lateinit var trabajo: EditText
    private lateinit var descripcionTrabajo: EditText
    private lateinit var requisitosTrabajo: EditText
    private lateinit var funcionesTrabajo: EditText
    private lateinit var beneficiosTrabajo: EditText
    private lateinit var ciudades: Spinner
    private lateinit var tipoEmpleo: Spinner

    private lateinit var modificarEmpleo: MaterialButton



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_empleo)

         email = intent.getStringExtra("email").toString()
         idEmpleo = intent.getStringExtra("idEmpleo").toString()

        modificarEmpleo = findViewById(R.id.btn_modificar)
        regresar = findViewById(R.id.regresar1)

        trabajo = findViewById(R.id.trabajo)
        descripcionTrabajo = findViewById(R.id.descripcionTrabajo)
        requisitosTrabajo = findViewById(R.id.requisitosTrabajo)
        funcionesTrabajo = findViewById(R.id.funcionesTrabajo)
        beneficiosTrabajo = findViewById(R.id.beneficiosTrabajo)
        ciudades = findViewById(R.id.ciudades)
        tipoEmpleo = findViewById(R.id.tipoEmpleo)

        regresar.setOnClickListener {
            finish()
        }


        // Cargar las ciudades desde el recurso de strings
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.ciudades_la_libertad,
            android.R.layout.simple_spinner_item
        )

        // Configurar el estilo de dropdown
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        // Asignar el adaptador al Spinner
        ciudades.adapter = adapter

        // Cargar las ciudades desde el recurso de strings
        val adapter2 = ArrayAdapter.createFromResource(
            this,
            R.array.tipo_de_empleos,
            android.R.layout.simple_spinner_item
        )

        // Configurar el estilo de dropdown
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


        // Asignar el adaptador al Spinner
        tipoEmpleo.adapter = adapter2



        if(email.isNotEmpty()){
            showEmailToast(email)
            recuperarNombre(email)
            recuperarFecha()
            cargarInfo()
        }

        modificarEmpleo.setOnClickListener{
            setup()
        }

    }

    private fun recuperarFecha() {
        // Accede a la colección 'Usuarios' y busca el documento por el correo
        db.collection("Empleos").document(idEmpleo).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Obtén el nombre del documento
                    fechaEmpleo = document.getString("fechaPublicación").toString()
                    Toast.makeText(this, "La fecha almacenada es: $fechaEmpleo", Toast.LENGTH_LONG).show()

                } else {
                    println("No se encontró la fecha.")
                }
            }
            .addOnFailureListener { exception ->
                println("Error al obtener el usuario: ${exception.message}")
            }

    }

    private fun recuperarNombre(email: String) {
        // Accede a la colección 'Usuarios' y busca el documento por el correo
        db.collection("Empresa").document(email).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Obtén el nombre del documento
                    nombreEmpresa = document.getString("nombre").toString()
                    Toast.makeText(this, "El nombre almacenado es: $nombreEmpresa", Toast.LENGTH_LONG).show()

                } else {
                    println("No se encontró el usuario con ese correo.")
                }
            }
            .addOnFailureListener { exception ->
                println("Error al obtener el usuario: ${exception.message}")
            }

    }

    private fun setup() {
        val tra: String = trabajo.text.toString()
        val desc: String = descripcionTrabajo.text.toString()
        val requi: String = requisitosTrabajo.text.toString()
        val func: String = funcionesTrabajo.text.toString()
        val ben: String = beneficiosTrabajo.text.toString()
        val ciudadSeleccionada = ciudades.selectedItem.toString()
        val tipoEmpleoSeleccionado = tipoEmpleo.selectedItem.toString()

        val empleosData = hashMapOf(

            "trabajo" to tra,
            "descripcion" to desc,
            "requisitos" to requi,
            "funciones" to func,
            "beneficios" to ben,
            "ciudad" to ciudadSeleccionada,
            "tipoEmpleo" to tipoEmpleoSeleccionado,
            "CorreoEmpresa" to email,
            "fechaPublicación" to fechaEmpleo,
            "nombreEmpresa" to nombreEmpresa
        )

        // Añadimos los datos a la colección 'Empresa', identificando por el correo
        FirebaseFirestore.getInstance().collection("Empleos")
            .document(idEmpleo)
            .set(empleosData)
            .addOnSuccessListener {
                Toast.makeText(this, "Datos modificados", Toast.LENGTH_SHORT).show()
                Log.d("EditarEmpleo", "Ciudad seleccionada: $ciudadSeleccionada, Tipo de empleo: $tipoEmpleoSeleccionado")

            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }




    }

    private fun cargarInfo() {
        // Consulta la base de datos usando el id proporcionado
        db.collection("Empleos").document(idEmpleo).get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Obtener los datos
                val tra = document.getString("trabajo") ?: ""
                val des = document.getString("descripcion") ?: ""
                val requi = document.getString("requisitos") ?: ""
                val funci = document.getString("funciones") ?: ""
                val bene = document.getString("beneficios") ?: ""
                val ciu = document.getString("ciudad") ?: ""
                val tip = document.getString("tipoEmpleo") ?: ""

                trabajo.setText(tra)
                descripcionTrabajo.setText(des)
                requisitosTrabajo.setText(requi)
                funcionesTrabajo.setText(funci)
                beneficiosTrabajo.setText(bene)

                // Cargar los arrays de recursos
                val ciudadesList = resources.getStringArray(R.array.ciudades_la_libertad).toList()
                val tipoEmpleoList = resources.getStringArray(R.array.tipo_de_empleos).toList()

                // Seleccionar ciudad en el Spinner
                val ciudadIndex = ciudadesList.indexOf(ciu)
                if (ciudadIndex >= 0) {
                    ciudades.setSelection(ciudadIndex)
                }
                // Seleccionar tipo de empleo en el Spinner
                val tipoIndex = tipoEmpleoList.indexOf(tip)
                if (tipoIndex >= 0) {
                    tipoEmpleo.setSelection(tipoIndex)
                }


            } else {
                Log.d("Firestore", "El documento no existe")
            }
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Error al cargar los datos", e)
            Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
        }

    }

    private fun showEmailToast(email: String?) {
        Toast.makeText(this, "El correo almacenado es: $email", Toast.LENGTH_LONG).show()
    }
}