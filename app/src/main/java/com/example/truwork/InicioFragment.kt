package com.example.truwork

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.truwork.adaptadores.AdapterEmpleos
import com.example.truwork.databinding.FragmentInicioBinding
import com.google.firebase.firestore.FirebaseFirestore

class InicioFragment : Fragment(R.layout.fragment_inicio) {

    private val db = FirebaseFirestore.getInstance()

    private var email: String = ""
    //private lateinit var email: String

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var buscar: EditText

    //Mostrar lista de empleos
    private val empleoList = mutableListOf<EmpleosGeneral>()
    private val filteredList = mutableListOf<EmpleosGeneral>()
    private lateinit var empleoAdapter: AdapterEmpleos
    private lateinit var recyclerView: RecyclerView

    private var _binding: FragmentInicioBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentInicioBinding.inflate(inflater, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("TruSafePreferences", AppCompatActivity.MODE_PRIVATE)

        // Recuperar el correo almacenado en SharedPreferences
        email = sharedPreferences.getString("email", "") ?: ""
        // Primero, intentamos obtener el correo del Intent
        email = arguments?.getString("email") ?: ""
        // Si el correo está vacío, intenta recuperar el correo almacenado en SharedPreferences
        if (email.isEmpty()) {
            email = sharedPreferences.getString("email", "") ?: ""
        }
        //Toast.makeText(requireContext(), "El correo almacenado en FRAGMENT ES: $email", Toast.LENGTH_LONG).show()

        // Mostrar el correo para verificar
        Log.d("CorreoInicio", "Correo recuperado: $email")

        buscar = binding.buscar

        // Configuración de RecyclerView
        recyclerView = binding.recyclerView
        empleoAdapter = AdapterEmpleos(empleoList, email)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = empleoAdapter




        // Configurar búsqueda en el EditText
        buscar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                buscarEmpleos() // Llama a la función de búsqueda
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // No es necesario hacer nada aquí
            }
        })



        return binding.root
    }

    private fun buscarEmpleos() {
        val query = buscar.text.toString().trim() // Asegúrate de obtener el texto del EditText
        Log.d("Filtrado", "Texto en buscar: $query") // Log para verificar el texto
        filtrarEmpleos(query) // Llama a filtrarEmpleos con el texto correcto
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cargarinfo() // Cargar los empleos al crear la vista

        // Configurar la acción de búsqueda en el teclado
        buscar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = buscar.text.toString().trim()
                filtrarEmpleos(query) // Ejecutar la función de búsqueda
                true
            } else {
                false
            }
        }
    }

    private var originalList = mutableListOf<EmpleosGeneral>() // Lista original

    private fun cargarinfo() {
        db.collection("Empleos")
            .get()
            .addOnSuccessListener { documents ->
                empleoList.clear() // Limpiar antes de llenar
                for (document in documents) {
                    val id = document.id
                    val nombreCargo = document.getString("trabajo") ?: ""
                    val empresaNombre = document.getString("nombreEmpresa") ?: ""
                    val nombreCiudad = document.getString("ciudad") ?: ""
                    val nombreTiempo = document.getString("tipoEmpleo") ?: ""
                    val fechaPublicacion = document.getString("fechaPublicación") ?: ""

                    val empleosvisibles = EmpleosGeneral(id, nombreCargo, empresaNombre, nombreCiudad, nombreTiempo, fechaPublicacion)
                    empleoList.add(empleosvisibles)
                }
                originalList = empleoList.toMutableList() // Copia la lista original
                Log.d("MisEmpleos", "Total de empleos cargados: ${empleoList.size}")
                Log.d("MisEmpleosContenido", "Contenido de empleoList: $empleoList")

                // Filtrar empleos al cargar
                filtrarEmpleos(buscar.text.toString())
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error al obtener empleos: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("Mis empleos", "Error al obtener Empleos: ${exception.message}")
            }
    }





    private fun filtrarEmpleos(query: String) {
        filteredList.clear() // Limpia la lista filtrada
        Log.d("Filtrado", "Filtrando con query: $query")

        if (query.isEmpty()) {
            Log.d("Filtrado", "Consulta vacía, agregando todos los empleos")
            filteredList.addAll(originalList) // Agrega todos los empleos desde la lista original
        } else {
            originalList.forEach { empleo -> // Cambiar empleoList por originalList
                Log.d("Filtrado", "Comprobando: ${empleo.cargoNombre} contra: $query")
                if (empleo.cargoNombre.contains(query, ignoreCase = true)) {
                    Log.d("Filtrado", "Agregando: ${empleo.cargoNombre}")
                    filteredList.add(empleo) // Agrega solo los que coincidan
                }
            }
        }

        Log.d("Filtrado", "Total de empleos filtrados: ${filteredList.size}")
        empleoAdapter.updateList(filteredList) // Actualiza el adaptador
    }





    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}