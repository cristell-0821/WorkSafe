package com.example.truwork

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.truwork.adaptadores.AdapterPostulaciones
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage




class VerPostulaciones : AppCompatActivity() {

    companion object {
        private const val CREATE_FILE_REQUEST_CODE = 1001
    }

    private lateinit var email: String
    private lateinit var correoCliente: String
    private lateinit var sharedPreferences: SharedPreferences

    private val postulacioneList = mutableListOf<Postulaciones>()
    private lateinit var postulacionesAdapter: AdapterPostulaciones
    private lateinit var recyclerView: RecyclerView

    private val db = FirebaseFirestore.getInstance()
    private lateinit var empleoId: String

    private lateinit var regresar: ImageButton

    private lateinit var verEmpleo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_postulaciones)


        // Obtener SharedPreferences
        sharedPreferences = getSharedPreferences("TruSafePreferences", MODE_PRIVATE)

        // Recuperar el correo del Intent o SharedPreferences
        email = intent.getStringExtra("email") ?: sharedPreferences.getString("email", "") ?: ""


        empleoId = intent.getStringExtra("idEmpleo") ?: "" // Recupera el ID del empleo


        regresar = findViewById(R.id.regresar2)

        regresar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
            finish()
        }

        verEmpleo = findViewById(R.id.nombreEmpleo)

        recyclerView = findViewById(R.id.recyclerView)
        postulacionesAdapter = AdapterPostulaciones(postulacioneList, this)
        recyclerView.adapter = postulacionesAdapter


        // Configurar el LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)


        //Toast.makeText(this, "Id es $empleoId", Toast.LENGTH_SHORT).show()

        cargarPostulantes()

    }

    // VerPostulaciones.kt
    // VerPostulaciones.kt
    fun descargarCV(correoCliente: String) {
        // Define la referencia del archivo en Firebase Storage
        val cvRef = FirebaseStorage.getInstance().reference.child("CVs/$correoCliente.pdf")
        Log.e("Correos", "Intentando descargar CV para: $correoCliente")

        // Verifica si el archivo existe
        cvRef.metadata.addOnSuccessListener { metadata ->
            // Si metadata no es null, el archivo existe
            // Abre un selector de archivos para que el usuario elija la ubicación para guardar el CV
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                type = "application/pdf" // Establece el tipo de archivo
                putExtra(Intent.EXTRA_TITLE, "$correoCliente.pdf") // Sugiere un nombre para el archivo
            }
            startActivityForResult(intent, CREATE_FILE_REQUEST_CODE)

            // Descarga los bytes del CV
            cvRef.getBytes(metadata.sizeBytes).addOnSuccessListener { bytes ->
                // Guarda los bytes en un atributo de la clase para usarlo en onActivityResult
                savedBytes = bytes // Agrega esta línea
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error al descargar el CV: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            // Si falla la obtención de metadata, el archivo no existe
            Toast.makeText(this, "No se encontró un CV para este postulante.", Toast.LENGTH_SHORT).show()
        }
    }

    // Variable para almacenar los bytes del CV descargado
    private var savedBytes: ByteArray? = null

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = data?.data
            savedBytes?.let { bytes ->
                uri?.let {
                    saveToFile(it, bytes)
                }
            }
        }
    }

    private fun saveToFile(uri: Uri, bytes: ByteArray) {
        // Usa un OutputStream para guardar el contenido del CV en el archivo elegido
        contentResolver.openOutputStream(uri).use { outputStream ->
            outputStream?.write(bytes)
        }
    }




    private fun cargarPostulantes() {
        db.collection("Postulaciones")
            .whereEqualTo("empleoId", empleoId)
            .get()
            .addOnSuccessListener { documents ->
                // Limpiar la lista al inicio
                postulacioneList.clear()

                if (documents.isEmpty) {
                    Toast.makeText(this, "No hay postulantes para este empleo", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                for (document in documents) {
                    val empleoId = document.getString("empleoId") ?: ""
                     correoCliente = document.getString("postulanteEmail") ?: ""

                    if (empleoId.isNotEmpty() && correoCliente.isNotEmpty()) {
                        Toast.makeText(this, "El Id es $empleoId", Toast.LENGTH_SHORT).show()
                        cargarDetallesDelPostulante(empleoId, correoCliente)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al obtener los datos: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("Mis empleos", "Error al obtener los datos: ${exception.message}")
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun cargarDetallesDelPostulante(empleoId: String, correoCliente: String) {
        db.collection("Empleos").document(empleoId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Obtener los datos del empleo
                    val nombre = document.getString("trabajo") ?: ""
                    verEmpleo.text = nombre

                    // Obtener detalles del usuario
                    db.collection("Usuarios").document(correoCliente).get()
                        .addOnSuccessListener { userDocument ->
                            if (userDocument.exists()) {
                                val nombreUsuario = userDocument.getString("Nombres") ?: ""
                                val apellidoUsuario = userDocument.getString("Apellidos") ?: ""
                                val telefono = userDocument.getString("Telefono") ?: ""

                                val nombreCompleto = "$nombreUsuario $apellidoUsuario"

                                // Crear un nuevo contacto
                                val nuevoPostulante = Postulaciones(nombreCompleto, correoCliente, telefono)
                                postulacioneList.add(nuevoPostulante)

                                // Notificar cambios al adaptador una vez que se agrega el postulante
                                postulacionesAdapter.notifyDataSetChanged()
                            }
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Error al obtener usuario: ${exception.message}", Toast.LENGTH_SHORT).show()
                            Log.e("Mis empleos", "Error al obtener usuario: ${exception.message}")
                        }
                } else {
                    Log.d("Firestore", "El documento del empleo no existe")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al cargar los datos del empleo", e)
                Toast.makeText(this, "Error al cargar los datos del empleo", Toast.LENGTH_SHORT).show()
            }
    }



}
