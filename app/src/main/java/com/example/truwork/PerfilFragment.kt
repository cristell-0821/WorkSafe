package com.example.truwork

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.truwork.databinding.FragmentPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class PerfilFragment : Fragment(R.layout.fragment_perfil) {

    private  var _binding: FragmentPerfilBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    private lateinit var email: String
    private lateinit var sharedPreferences: SharedPreferences



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("TruSafePreferences", AppCompatActivity.MODE_PRIVATE)

        // Recuperar el correo almacenado en SharedPreferences
        email = sharedPreferences.getString("email", "") ?: ""

        // Primero, intentamos obtener el correo del Intent
        email = arguments?.getString("email") ?: ""

        // Si el correo está vacío, intenta recuperar el correo almacenado en SharedPreferences
        if (email.isEmpty()) {
            email = sharedPreferences.getString("email", "") ?: ""
        }

        Toast.makeText(requireContext(), "El correo almacenado en FRAGMENT ES: $email", Toast.LENGTH_LONG).show()





        binding.abrirMiPerfil.setOnClickListener {
            // Iniciar la nueva actividad
            val intent = Intent(requireContext(), MiPerfil::class.java).apply {
                putExtra("email", email)
            }

            // Crear las opciones de actividad con las animaciones
            val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.slide_in_right, R.anim.slide_out_left)

            // Iniciar la nueva actividad con las opciones
            startActivity(intent, options.toBundle())
        }

        binding.PerfilEmpresa.setOnClickListener {

            // Iniciar la nueva actividad
            val intent = Intent(requireContext(), MiEmpresa::class.java).apply {
                putExtra("email", email)
            }

            // Crear las opciones de actividad con las animaciones
            val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.slide_in_right, R.anim.slide_out_left)

            // Iniciar la nueva actividad con las opciones
            startActivity(intent, options.toBundle())
        }
        binding.MisEmpleos.setOnClickListener {
            // Iniciar la nueva actividad
            val intent = Intent(requireContext(), MisEmpleos::class.java).apply {
                putExtra("email", email)
            }

            // Crear las opciones de actividad con las animaciones
            val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.slide_in_right, R.anim.slide_out_left)

            // Iniciar la nueva actividad con las opciones
            startActivity(intent, options.toBundle())

        }
        binding.Soporte.setOnClickListener {
            val phoneNumber = "977972282"
            val message = "¡Hola! Quería contactarte."

            // Crear un Intent para abrir WhatsApp con el número y mensaje
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}")
            startActivity(intent)
        }
        binding.Terminos.setOnClickListener {
            // Iniciar la nueva actividad
            val intent = Intent(requireContext(), TerminosCondiciones::class.java).apply {
                putExtra("email", email)
            }

            // Crear las opciones de actividad con las animaciones
            val options = ActivityOptions.makeCustomAnimation(requireContext(), R.anim.slide_in_right, R.anim.slide_out_left)

            // Iniciar la nueva actividad con las opciones
            startActivity(intent, options.toBundle())

        }



        binding.CerrarSesion.setOnClickListener {

            // Eliminar el correo almacenado en SharedPreferences
            val sharedPreferences = requireActivity().getSharedPreferences("TruSafePreferences",
                AppCompatActivity.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.remove("email")  // Eliminar el correo almacenado
            editor.apply()

            // Cerrar sesión en Firebase
            FirebaseAuth.getInstance().signOut()

            // Redirigir al Login
            val intent = Intent(requireContext(), Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish() // Finaliza la actividad actual


        }

        if (email.isNotEmpty()) {
            // Si el correo está disponible, cargar la información
            cargarinfo()
        } else {
            Toast.makeText(requireContext(), "Correo no disponible", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }





    @SuppressLint("SetTextI18n")
    private fun cargarinfo() {
        // Consulta la base de datos usando el correo proporcionado
        db.collection("Usuarios").document(email).get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Obtener los datos
                val nombres = document.getString("Nombres") ?: ""
                val apellidos = document.getString("Apellidos") ?: ""

                // Log para depuración
                Log.d("Firestore", "Nombres: $nombres, Apellidos: $apellidos")

                // Establecer los valores en los TextView
                binding.nombreajustes.text = "$nombres $apellidos"  // Usar template para una mejor legibilidad
            } else {
                Log.d("Firestore", "El documento no existe")
            }
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Error al cargar los datos", e)
            Toast.makeText(context, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}