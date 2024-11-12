package com.example.truwork

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class MiPerfil : AppCompatActivity() {


    private lateinit var email: String
    private lateinit var sharedPreferences: SharedPreferences
    private val db = FirebaseFirestore.getInstance()
    private val storageRef = FirebaseStorage.getInstance().reference

    private lateinit var guardar: MaterialButton
    private lateinit var regresar: ImageButton

    private lateinit var nombreCliente: EditText
    private lateinit var apellidoCliente: EditText
    private lateinit var telefonoCliente: EditText
    private lateinit var profesionCliente: EditText
    private lateinit var presentacionCliente: EditText

    private val PICK_CV_REQUEST = 1
    private var cvUri: Uri? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mi_perfil)


        val subirCv = findViewById<CardView>(R.id.btn_subir_cv)

        nombreCliente = findViewById(R.id.nombreCliente)
        apellidoCliente = findViewById(R.id.apellidoCliente)
        telefonoCliente = findViewById(R.id.telefonoCliente)
        profesionCliente = findViewById(R.id.profesionCliente)
        presentacionCliente = findViewById(R.id.presentacionCliente)

        regresar = findViewById(R.id.btn_regresarmenu)
        guardar = findViewById(R.id.btn_guardar)

        regresar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
            finish()

        }

        guardar.setOnClickListener {
            setup()
        }

        // Obtener SharedPreferences
        sharedPreferences = getSharedPreferences("TruSafePreferences", MODE_PRIVATE)

        // Recuperar el correo del Intent o SharedPreferences
        email = intent.getStringExtra("email") ?: sharedPreferences.getString("email", "") ?: ""

        // Mostrar Toast si el correo está disponible
        if (email.isNotEmpty()) {
            showEmailToast(email)
            cargarInfo()
        }else {
            Toast.makeText(this, "Correo no disponible", Toast.LENGTH_SHORT).show()
        }

        subirCv.setOnClickListener {
            // Aquí puedes abrir el selector de archivos o iniciar la acción de subir el CV
            // Abrir selector de archivos para seleccionar el CV
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf" // Solo permitirá archivos PDF
            startActivityForResult(intent, PICK_CV_REQUEST)
        }


        // Verificar si tiene un CV subido
        verificarCV(email)


    }

    private fun verificarCV(correoUsuario: String) {
        val cvRef = FirebaseStorage.getInstance().reference.child("CVs/$correoUsuario.pdf")

        cvRef.metadata.addOnSuccessListener {
            // Si se obtiene metadata, el CV existe
            mostrarCVSubido()
        }.addOnFailureListener {
            // Si falla, significa que el CV no existe
            mostrarCVNoSubido()
        }
    }

    private fun mostrarCVSubido() {
        val textCVStatus = findViewById<TextView>(R.id.textCVStatus)
        textCVStatus.text = "CV subido"
        textCVStatus.visibility = View.VISIBLE
    }

    private fun mostrarCVNoSubido() {
        val textCVStatus = findViewById<TextView>(R.id.textCVStatus)
        textCVStatus.text = "No se ha subido un CV"
        textCVStatus.visibility = View.VISIBLE
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CV_REQUEST && resultCode == Activity.RESULT_OK) {
            cvUri = data?.data // Obtener la URI del archivo seleccionado

            cvUri?.let {
                uploadCv(it)
            } ?: run {
                Toast.makeText(this, "Error al seleccionar el archivo", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadCv(uri: Uri) {
        val cvRef = storageRef.child("CVs/$email.pdf") // Ruta donde se guardará el CV

        cvRef.putFile(uri)
            .addOnSuccessListener {
                Toast.makeText(this, "CV subido exitosamente", Toast.LENGTH_SHORT).show()
                 verificarCV(email)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al subir el CV: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setup() {
        val nombre: String = nombreCliente.text.toString()
        val apellido: String = apellidoCliente.text.toString()
        val telefono: String = telefonoCliente.text.toString()
        val profesion: String = profesionCliente.text.toString()
        val presentacion: String = presentacionCliente.text.toString()

        val datosCliente = hashMapOf(
            "Nombres" to nombre,
            "Apellidos" to apellido,
            "Telefono" to telefono,
            "Profesion" to profesion,
            "Presentacion" to presentacion
        )

        // Añadimos los datos a la colección 'Empresa', identificando por el correo
        FirebaseFirestore.getInstance().collection("Usuarios")
            .document(email)
            .set(datosCliente)
            .addOnSuccessListener {
                Toast.makeText(this, "Datos del cliente guardados", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    private fun cargarInfo() {
// Consulta la base de datos usando el correo proporcionado
        db.collection("Usuarios").document(email).get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Obtener los datos
                val nombre = document.getString("Nombres") ?: ""
                val apellidos = document.getString("Apellidos") ?: ""
                val telefono = document.getString("Telefono") ?: ""
                val profesion = document.getString("Profesion") ?: ""
                val presentacion = document.getString("Presentacion") ?: ""

                nombreCliente.setText(nombre)
                apellidoCliente.setText(apellidos)
                telefonoCliente.setText(telefono)
                profesionCliente.setText(profesion)
                presentacionCliente.setText(presentacion)


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