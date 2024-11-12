package com.example.truwork

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {

    private lateinit var iniciarsesion: Button
    private lateinit var crearcuenta: TextView
    private lateinit var recuperarCuenta: TextView
    private lateinit var recordar: CheckBox
    private var correo: EditText? = null
    private var contra: EditText? = null


    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(800)
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences("TruSafePreferences", MODE_PRIVATE)


        iniciarsesion = findViewById(R.id.buttom_iniciarsesion)
        crearcuenta = findViewById(R.id.crear_cuenta)
        recuperarCuenta = findViewById(R.id.recuperarCorreo)

        correo = findViewById(R.id.correo)
        contra = findViewById(R.id.contra)
        recordar = findViewById(R.id.recordar)

        // Verifica si ya hay un correo guardado en SharedPreferences
        val savedEmail = sharedPreferences.getString("email", "")
        if (!savedEmail.isNullOrEmpty()) {
            // Inicia directamente a MainActivity si hay un correo guardado
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Finaliza esta actividad
        }


        recuperarCuenta.setOnClickListener{
            startActivity(Intent(this, RecuperarContra::class.java))
        }



        crearcuenta.setOnClickListener {
            startActivity(Intent(this, CrearCuenta::class.java))
            finish()
        }

        iniciarsesion.setOnClickListener {
            iniciarSesion()
        }

    }

    private fun iniciarSesion() {
        val corr: String = correo?.text.toString()
        val cont: String = contra?.text.toString()

        if (corr.isNotEmpty() && cont.isNotEmpty()){
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(corr, cont)
                .addOnCompleteListener{task ->
                    if (task.isSuccessful){
                        val user = task.result?.user

                        if (user != null && user.isEmailVerified){
                            // Guardar el correo en SharedPreferences
                            if (recordar.isChecked){
                                saveLoginDetails(corr)
                            }
                            // Pasar el correo temporal a MainActivity
                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("email", corr) // Pasa el correo aquí
                            startActivity(intent)
                            finish() // Finaliza la actividad actual
                        } else {
                            FirebaseAuth.getInstance().signOut()
                            Toast.makeText(this, "Por favor, verifica tu correo electrónico antes de iniciar sesión.", Toast.LENGTH_SHORT).show()
                        }
                    }else {
                        showalert()
                    }
                }

        }else{
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveLoginDetails(email: String) {
        val editor = sharedPreferences.edit()
        editor.putString("email", email)  // Guardar solo el correo
        editor.apply()
    }

    private fun showalert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error en la autenticación del usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


}