package com.example.proyectocrm.screens

data class Campo(
    val etiqueta: String,
    val valor: String,
    val setValor: (String) -> Unit,  // Esta función actualizará el valor
    val soloLectura: Boolean = false
)