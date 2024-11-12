package com.example.truwork

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CargarEmpleo : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var email: String
    private lateinit var sharedPreferences: SharedPreferences

    //Obtener nombre de la empresa
    private lateinit var nombreEmpresa: String

    private lateinit var regresar: ImageView
    private lateinit var trabajo: EditText
    private lateinit var descripcionTrabajo: EditText
    private lateinit var requisitosTrabajo: EditText
    private lateinit var funcionesTrabajo: EditText
    private lateinit var beneficiosTrabajo: EditText
    private lateinit var ciudades: Spinner
    private lateinit var tipoEmpleo: Spinner

    private lateinit var registrarEmpleo: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cargar_empleo)


        // Obtener SharedPreferences
        sharedPreferences = getSharedPreferences("TruSafePreferences", MODE_PRIVATE)

        // Recuperar el correo del Intent o SharedPreferences
        email = intent.getStringExtra("email") ?: sharedPreferences.getString("email", "") ?: ""

        // Mostrar Toast si el correo está disponible
        if (email.isNotEmpty()) {
            showEmailToast(email)
            recuperarNombre(email)
        }


        registrarEmpleo = findViewById(R.id.btn_registrar)
        regresar = findViewById(R.id.regresar1)

        trabajo = findViewById(R.id.trabajo)
        descripcionTrabajo = findViewById(R.id.descripcionTrabajo)
        requisitosTrabajo = findViewById(R.id.requisitosTrabajo)
        funcionesTrabajo = findViewById(R.id.funcionesTrabajo)
        beneficiosTrabajo = findViewById(R.id.beneficiosTrabajo)
        ciudades = findViewById(R.id.ciudades)
        tipoEmpleo = findViewById(R.id.tipoEmpleo)

        regresar.setOnClickListener {
            val intent = Intent(this, MisEmpleos::class.java).apply {
            putExtra("email", email)
        }
            startActivity(intent)
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



        setup()


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

    private fun showEmailToast(email: String?) {
        Toast.makeText(this, "El correo almacenado es: $email", Toast.LENGTH_LONG).show()
    }

    private fun setup() {
        registrarEmpleo.setOnClickListener {

            val corr: String = email
            val nomEmpresa: String = nombreEmpresa
            // Obtener la fecha y el día del sistema
            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().time)


            // Captura de datos desde los campos de texto y spinners
            val trabajoText = trabajo.text.toString()
            val descripcionText = descripcionTrabajo.text.toString()
            val requisitosText = requisitosTrabajo.text.toString()
            val funcionesText = funcionesTrabajo.text.toString()
            val beneficiosText = beneficiosTrabajo.text.toString()
            val ciudadSeleccionada = ciudades.selectedItem.toString()
            val tipoEmpleoSeleccionado = tipoEmpleo.selectedItem.toString()

            // Verificar si algún campo está vacío
            if (trabajoText.isBlank() || descripcionText.isBlank() || requisitosText.isBlank() || funcionesText.isBlank() ||
                beneficiosText.isBlank() || ciudadSeleccionada.isBlank() || tipoEmpleoSeleccionado.isBlank()) {

                Toast.makeText(this, "Por favor, complete todos los campos antes de registrar el empleo.", Toast.LENGTH_SHORT).show()
            } else{

                // Crear un mapa con los datos para agregar a Firestore
                val empleo = hashMapOf(
                    "trabajo" to trabajoText,
                    "descripcion" to descripcionText,
                    "requisitos" to requisitosText,
                    "funciones" to funcionesText,
                    "beneficios" to beneficiosText,
                    "ciudad" to ciudadSeleccionada,
                    "tipoEmpleo" to tipoEmpleoSeleccionado,
                    "CorreoEmpresa" to corr,
                    "nombreEmpresa" to nomEmpresa,
                    "fechaPublicación" to currentDate
                )

                // Agregar los datos a Firestore en la colección "Empleos"
                db.collection("Empleos").add(empleo)
                    .addOnSuccessListener {
                        // Registro exitoso, muestra mensaje o realiza acción
                        Toast.makeText(this, "Empleo registrado exitosamente", Toast.LENGTH_SHORT).show()

                        // Limpiar los campos después del registro
                        trabajo.text.clear()
                        descripcionTrabajo.text.clear()
                        requisitosTrabajo.text.clear()
                        funcionesTrabajo.text.clear()
                        beneficiosTrabajo.text.clear()
                        ciudades.setSelection(0)  // Resetea el Spinner a la primera posición
                        tipoEmpleo.setSelection(0)  // Resetea el Spinner a la primera posición
                    }
                    .addOnFailureListener { e ->
                        // Error en el registro, muestra mensaje o realiza acción
                        Toast.makeText(this, "Error al registrar empleo: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

            }


        }
    }

}
