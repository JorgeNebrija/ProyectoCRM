package com.example.proyectocrm.screens

// Clase de datos que representa una cita
data class Cita(
    val nombre: String = "",     // Nombre del cliente
    val dni: String = "",        // DNI del cliente
    val direccion: String = "",  // Dirección de la cita
    val hora: String = ""        // Hora de la cita
)
