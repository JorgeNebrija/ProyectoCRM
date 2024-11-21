package com.example.proyectocrm.screens

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.proyectocrm.R
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


@Composable
fun PantallaCalendario(navController: NavHostController) {
    ScaffoldCalendario(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldCalendario(navController: NavHostController) {
    var mostrarDialogo by remember { mutableStateOf(false) } // Estado para mostrar el diálogo
    var listaCitas by remember { mutableStateOf<List<Cita>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) } // Estado para controlar la carga

    LaunchedEffect(Unit) {
        // Cambiar el estado a cargando mientras se obtienen las citas
        cargando = true
        try {
            val citas = getCitas() // Llamada a la función para obtener todas las citas
            listaCitas = citas // Asignar lista de citas
        } catch (e: Exception) {
            println("Error al cargar citas: ${e.message}")
        } finally {
            cargando = false // Cambiar a false después de la carga
        }
    }
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        "Calendario Citas",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,

                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            BottomCalendario(navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = Color(0xFFE4E4E4))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Barra de búsqueda
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.calendarioicono),
                    contentDescription = "Crear Nueva Cita",
                    tint = Color.Black,
                    modifier = Modifier.size(35.dp)
                )
                // Texto fijo: "Crear Nueva Cita"
                Text(
                    text = "Crear Nueva Cita",
                    color = Color.Black,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
                Button(
                    onClick = { mostrarDialogo = true }, // Mostrar diálogo al hacer clic
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0756FF)),
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    Text("+")
                }
            }
            // Mostrar un indicador de carga mientras se cargan las citas
            if (cargando) {
                CircularProgressIndicator() // Indicador de carga
            } else if (listaCitas.isEmpty()) {
                // Mostrar mensaje si la lista está vacía
                Text(
                    text = "No hay citas registradas.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp
                )
            } else {
                // Mostrar lista de citas si no está vacía
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listaCitas) { cita ->
                        CitaRow(cita) // Mostrar cada cita en una fila
                    }
                }
            }

}
            // Mostrar el diálogo si mostrarDialogo es true
            if (mostrarDialogo) {
                DialogoNuevaCita(onDismiss = { mostrarDialogo = false })
            }

    }
}
@Composable
fun DialogoNuevaCita(onDismiss: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Nueva Cita") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Ingresa Nombre") }
                )
                TextField(
                    value = dni,
                    onValueChange = { dni = it },
                    label = { Text("Ingresa DNI") }
                )
                TextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Ingresa Dirección") }
                )
                TextField(
                    value = hora,
                    onValueChange = { hora = it },
                    label = { Text("Ingresa Hora") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    guardarCitaEnBaseDeDatos(nombre, dni, direccion, hora) // Guardar cita
                    onDismiss()
                    // Cerrar el diálogo
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0756FF)) // Botón azul
            ) {
                Text("Guardar Cita")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0756FF)) // Botón azul
            ) {
                Text("Cancelar")
            }
        }
    )
}

// Composable para mostrar una fila de la tabla de citas
@Composable
fun CitaRow(cita: Cita) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface) // Fondo blanco usando MaterialTheme
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "Nombre: ${cita.nombre}", fontSize = 16.sp)
                Text(text = "DNI: ${cita.dni}", fontSize = 14.sp)
                Text(text = "Dirección: ${cita.direccion}", fontSize = 14.sp)
                Text(text = "Hora: ${cita.hora}", fontSize = 14.sp)
            }
        }
    }
}
// Función suspendida para obtener todas las citas desde Firestore
suspend fun getCitas(): List<Cita> {
    val db = FirebaseFirestore.getInstance()
    val citasRef = db.collection("citas") // Conecta con la colección "citas"

    return try {
        val querySnapshot = citasRef.get().await() // Obtiene los documentos
        val citas = mutableListOf<Cita>() // Lista mutable para almacenar citas

        // Itera sobre cada documento y lo convierte a la clase `Cita`
        for (document in querySnapshot.documents) {
            val cita = document.toObject(Cita::class.java)
            cita?.let { citas.add(it) }
        }

        citas // Devuelve la lista de citas
    } catch (e: Exception) {
        println("Error al obtener citas: ${e.message}")
        emptyList() // Devuelve una lista vacía en caso de error
    }
}
fun guardarCitaEnBaseDeDatos(nombre: String, dni: String, direccion: String, hora: String) {
    val db = FirebaseFirestore.getInstance()
    val citasRef = db.collection("citas")

    // Crea un objeto con los datos de la cita
    val nuevaCita = hashMapOf(
        "nombre" to nombre,
        "dni" to dni,
        "direccion" to direccion,
        "hora" to hora
    )
}



@Composable
fun BottomCalendario(navController: NavHostController) {
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.buscarazul),
                    contentDescription = "Inicio",

                )
            },
            selected = false, // Marca "Inicio" como seleccionado
            onClick = { navController.navigate("pantallaBuscarClientes") }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.calendarioicono),
                    contentDescription = "Buscar",
                    tint = Color(0xFF0756FF)

                )
            },
            selected = false,
            onClick = { navController.navigate("pantallaCalendario") }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.iconobuscar),
                    contentDescription = "Perfil"
                )
            },
            selected = false,
            onClick = { navController.navigate("pantallaMiPerfil") }
        )
    }
}
