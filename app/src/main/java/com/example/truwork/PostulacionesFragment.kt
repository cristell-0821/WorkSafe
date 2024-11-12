package com.example.truwork

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.truwork.adaptadores.AdapterFav
import com.example.truwork.databinding.FragmentPostulacionesBinding
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class PostulacionesFragment : Fragment(R.layout.fragment_postulaciones) {

    private val db = FirebaseFirestore.getInstance()
    private var email: String = ""
    private lateinit var sharedPreferences: SharedPreferences

    // Lista de empleos favoritos
    private val favList = mutableListOf<Fav>()
    private lateinit var favAdapter: AdapterFav
    private lateinit var recyclerView: RecyclerView

    private var _binding: FragmentPostulacionesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostulacionesBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("TruSafePreferences", AppCompatActivity.MODE_PRIVATE)

        // Recuperar el correo almacenado en SharedPreferences
        email = sharedPreferences.getString("email", "") ?: ""
        // Si no está en SharedPreferences, intentar obtenerlo desde los argumentos
        email = arguments?.getString("email") ?: email

        // Verificar que el correo esté disponible
        if (email.isNotEmpty()) {
            Toast.makeText(requireContext(), "Correo es $email", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Correo no disponible", Toast.LENGTH_SHORT).show()
        }

        // Configuración del RecyclerView y el adaptador
        recyclerView = binding.recyclerView
        favAdapter = AdapterFav(favList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = favAdapter

        // Cargar la información
        if (email.isNotEmpty()) {
            cargarinfo()
        }

        return binding.root
    }

    // Obtener todos los empleoIds del postulanteEmail
    private val empleoIds = mutableListOf<String>()

    private fun cargarinfo() {
        db.collection("Postulaciones")
            .whereEqualTo("postulanteEmail", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val empleoId = document.getString("empleoId")
                    empleoId?.let { empleoIds.add(it) }
                }
                // Obtener los empleos con los empleoIds obtenidos
                obtenerEmpleosConIds(empleoIds)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error al obtener postulaciones: ${exception.message}")
            }
    }

    // Buscar los empleos usando los empleoIds
    private fun obtenerEmpleosConIds(empleoIds: List<String>) {
        if (empleoIds.isEmpty()) return

        db.collection("Empleos")
            .whereIn(FieldPath.documentId(), empleoIds)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot.documents) {
                    val id = document.id
                    val nombreCargo = document.getString("trabajo") ?: ""
                    val empresaNombre = document.getString("nombreEmpresa") ?: ""
                    val nombreCiudad = document.getString("ciudad") ?: ""
                    val nombreTiempo = document.getString("tipoEmpleo") ?: ""
                    val fechaPublicacion = document.getString("fechaPublicación") ?: ""

                    // Crear un objeto Fav y agregarlo a la lista
                    val favvisibles = Fav(id, nombreCargo, empresaNombre, nombreCiudad, nombreTiempo, fechaPublicacion)
                    favList.add(favvisibles)
                }
                // Notificar al adaptador que los datos han cambiado
                favAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error al obtener empleos: ${exception.message}")
            }
    }
}
