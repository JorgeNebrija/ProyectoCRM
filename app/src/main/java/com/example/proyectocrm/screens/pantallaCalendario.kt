package com.example.proyectocrm.screens

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
    var mostrarDialogo by remember { mutableStateOf(false) }
    var listaCitas by remember { mutableStateOf<List<Cita>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    // Cargar citas al inicio
    LaunchedEffect(Unit) {
        cargando = true
        try {
            val citas = getCitas()
            listaCitas = citas
        } catch (e: Exception) {
            println("Error al cargar citas: ${e.message}")
        } finally {
            cargando = false
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
                Text(
                    text = "Crear Nueva Cita",
                    color = Color.Black,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
                Button(
                    onClick = { mostrarDialogo = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0756FF)),
                    modifier = Modifier.padding(start = 8.dp),
                ) {
                    Text("+")
                }
            }

            // Indicador de carga o mensaje de no citas
            if (cargando) {
                CircularProgressIndicator()
            } else if (listaCitas.isEmpty()) {
                Text(
                    text = "No hay citas registradas.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 18.sp
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listaCitas) { cita ->
                        CitaRow(cita)
                    }
                }
            }
        }

        // Mostrar el diálogo
        if (mostrarDialogo) {
            DialogoNuevaCita(onDismiss = { mostrarDialogo = false })
        }
    }
}

@Composable
fun CitaRow(cita: Cita) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Nombre: ${cita.nombre}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "DNI: ${cita.dni}",
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Dirección: ${cita.direccion}",
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Hora: ${cita.hora}",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.calendarioicono),
                contentDescription = "Cita Confirmada",
                tint = Color(0xFF0756FF),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(24.dp)
            )
        }
    }
}

suspend fun getCitas(): List<Cita> {
    val db = FirebaseFirestore.getInstance()
    val citasRef = db.collection("citas")

    return try {
        val querySnapshot = citasRef.get().await()
        val citas = mutableListOf<Cita>()

        for (document in querySnapshot.documents) {
            val cita = document.toObject(Cita::class.java)
            cita?.let { citas.add(it) }
        }

        citas
    } catch (e: Exception) {
        println("Error al obtener citas: ${e.message}")
        emptyList()
    }
}

fun guardarCitaEnBaseDeDatos(nombre: String, dni: String, direccion: String, hora: String) {
    val db = FirebaseFirestore.getInstance()
    val citasRef = db.collection("citas")

    val nuevaCita = hashMapOf(
        "nombre" to nombre,
        "dni" to dni,
        "direccion" to direccion,
        "hora" to hora
    )

    citasRef.add(nuevaCita)
        .addOnSuccessListener {
            println("Cita guardada con éxito.")
        }
        .addOnFailureListener { e ->
            println("Error al guardar la cita: ${e.message}")
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
                    guardarCitaEnBaseDeDatos(nombre, dni, direccion, hora)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0756FF))
            ) {
                Text("Guardar Cita")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0756FF))
            ) {
                Text("Cancelar")
            }
        }
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
