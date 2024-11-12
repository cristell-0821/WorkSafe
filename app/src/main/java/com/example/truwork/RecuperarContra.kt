package com.example.truwork

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class RecuperarContra : AppCompatActivity() {

    private  var correo: EditText? = null
    private lateinit var recuperarContra: Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contra)

        correo = findViewById(R.id.correo)
        recuperarContra = findViewById(R.id.enviar_correo)

        auth = FirebaseAuth.getInstance()


        recuperarContra.setOnClickListener {
            sendPasswordResetEmail()
        }

    }

    private fun sendPasswordResetEmail() {
        val email = correo?.text.toString().trim()

        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show()
                    finish()
                    correo?.setText("")
                }
                else {
                    Toast.makeText(this, "Error al enviar el correo de recuperación", Toast.LENGTH_SHORT).show()
                }
            }

    }
}