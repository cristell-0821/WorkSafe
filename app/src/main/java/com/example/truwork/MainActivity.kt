package com.example.truwork

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.truwork.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var auth: FirebaseAuth //Para gestionar el usuario
    private lateinit var email: String
    private lateinit var sharedPreferences: SharedPreferences
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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


        setupNavegacion(email)

    }



    private fun showEmailToast(email: String?) {
        Toast.makeText(this, "El correo almacenado es: $email", Toast.LENGTH_LONG).show()
    }

    private fun setupNavegacion(email: String) {
        val bottomNavigationView = binding.bottomNavigationView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Vincular el BottomNavigationView con el NavController
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        // Crear un bundle con el email
        val bundle = Bundle().apply {
            putString("email", email)
        }

        // Navegar al fragmento inicial con el email
        navController.navigate(R.id.pagina_inicio, bundle)

        // Escuchar cuando se selecciona un item
        bottomNavigationView.setOnItemSelectedListener { item ->
            val selectedBundle = Bundle().apply {
                putString("email", email)
                Log.d("CorreoPasado", "Correo pasado: $email")
            }

            when (item.itemId) {
                R.id.menu_inicio -> {
                    navController.navigate(R.id.pagina_inicio, selectedBundle)
                    true
                }
                R.id.menu_postulaciones -> {
                    navController.navigate(R.id.pagina_postulaciones, selectedBundle)
                    true
                }
                R.id.menu_ajustes -> {
                    navController.navigate(R.id.pagina_ajustes, selectedBundle)
                    true
                }
                else -> false
            }
        }
    }



}