package com.example.proyectocrm.screens

import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.navigation.NavHostController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PantallaCalendario(navHostController: NavHostController) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Calendario
        CalendarHeader()

        // Lista de citas
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(appointments) { appointment ->
                AppointmentCard(appointment)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun CalendarHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color(0xFFEFEFEF)),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "L", color = Color.Gray)
        Text(text = "M", color = Color.Gray)
        Text(text = "X", color = Color.Gray)
        Text(text = "J", color = Color.Gray)
        Text(text = "V", color = Color.Gray)
        Text(text = "S", color = Color.Gray)
        Text(text = "D", color = Color.Gray)

        // Día seleccionado con fondo redondeado
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(Color.Blue, shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "27", color = Color.White)
        }
    }
}

@Composable
fun AppointmentCard(appointment: Appointment) {
    Card(
        shape = RoundedCornerShape(8.dp),
        //backgroundColor = Color(0xFFF5F5F5),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = appointment.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Cita: ${appointment.status}", color = Color.Gray, fontSize = 14.sp)
            }
            IconButton(onClick = { /* Acción del botón */ }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Opciones")
            }
        }
    }
}

data class Appointment(val name: String, val status: String)

val appointments = listOf(
    Appointment("Jorge Andrés López", "Libre"),
    Appointment("Andrés López", "Libre"),
    Appointment("Ana Pérez", "Reservada"),
    Appointment("Luis Gómez", "Libre")
)
