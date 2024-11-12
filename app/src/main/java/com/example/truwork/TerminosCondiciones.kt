package com.example.truwork

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class TerminosCondiciones : AppCompatActivity() {
    private lateinit var regresar: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_terminos_condiciones)

        regresar = findViewById(R.id.btn_regresarmenu)

        regresar.setOnClickListener {

            finish()

        }

    }


}