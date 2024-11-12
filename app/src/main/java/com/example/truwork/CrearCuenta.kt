package com.example.truwork

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CrearCuenta : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private lateinit var volverlogin: TextView
    private lateinit var registrarcuenta: Button

    private var nom: EditText? = null
    private var ape: EditText? = null
    private var telefono: EditText? = null
    private var correo: EditText? = null
    private var contra: EditText? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crear_cuenta)

        volverlogin = findViewById(R.id.volverlogin)
        registrarcuenta = findViewById(R.id.buttom_crearcuenta)

        nom = findViewById(R.id.nombre)
        ape = findViewById(R.id.apellidos)
        telefono = findViewById(R.id.telefono)
        correo = findViewById(R.id.crreo)
        contra = findViewById(R.id.contr)



        volverlogin.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
        setup()

    }


    private fun setup() {
        registrarcuenta.setOnClickListener {
            val nom: String = nom?.text.toString()
            val ape:String = ape?.text.toString()
            val tel: String = telefono?.text.toString()
            val corr: String = correo?.text.toString()
            val cont: String = contra?.text.toString()
            val presentacion = ""
            val profesion = ""
            val provider = "BASIC"

            if (isValidEmail(corr) && isValidPassword(cont)){
                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(corr, cont)
                    .addOnCompleteListener {task ->
                        if (task.isSuccessful){
                            val user = task.result?.user
                            user?.sendEmailVerification()?.addOnCompleteListener { verificationTask ->
                                if (verificationTask.isSuccessful) {
                                    if (nom.isNotEmpty() && ape.isNotEmpty() && tel.isNotEmpty()
                                        && corr.isNotEmpty() && cont.isNotEmpty()){
                                        db.collection("Usuarios").document(corr).set(
                                            hashMapOf(
                                                "Provider" to provider,
                                                "Address" to corr,
                                                "Nombres" to nom,
                                                "Apellidos" to ape,
                                                "Telefono" to tel,
                                                "Presentacion" to presentacion,
                                                "Profesion" to profesion
                                            )
                                        ).addOnSuccessListener {
                                            Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show()
                                        }.addOnFailureListener { e ->
                                            Toast.makeText(this, "Error al registrar usuario: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }else {
                                        Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                                    }
                                    Toast.makeText(this, "Correo de verificación enviado. Por favor, revisa tu bandeja de entrada.", Toast.LENGTH_SHORT).show()
                                    showHome()
                                }else {
                                    Toast.makeText(this, "Error al enviar el correo de verificación.", Toast.LENGTH_SHORT).show()
                                }

                            }

                        }else {
                            showAlert()
                        }

                    }

            }else {
                // Manejar el caso cuando los campos de correo y contraseña están vacíos
                Toast.makeText(this, "Por favor, completa los campos correctamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showHome() {
        startActivity(Intent(this, Login::class.java))
        finish()
    }



    private fun showAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error en la autenticación del usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun isValidEmail(correo: String): Boolean {
        val regex = Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}\$")
        return regex.matches(correo)
    }

    private fun isValidPassword(contrase: String): Boolean {
        return contrase.length >= 6
    }
}