package com.example.proyectocrm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.proyectocrm.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

@Composable
fun PantallaBuscarClientes(navController: NavHostController) {
    ScaffoldBuscarClientes(navController)
}

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ScaffoldBuscarClientes(navController: NavHostController) {
    // Estados para la búsqueda y los datos del cliente
    var searchText by remember { mutableStateOf("") }
    var cliente by remember { mutableStateOf<Map<String, String>?>(null) } // Cliente inicial nulo
    var buscando by remember { mutableStateOf(false) } // Para mostrar estado de búsqueda
    var mostrandoMensajeNoEncontrado by remember { mutableStateOf(false) } // Controla el mensaje "Cliente no encontrado"

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        "Buscar Clientes",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            BottomBuscarClientes(navController)
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
                    painter = painterResource(id = R.drawable.iconobuscar),
                    contentDescription = "Buscar",
                    tint = Color.Black
                )
                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (searchText.isEmpty()) {
                            Text(
                                text = "Buscar cliente por nombre",
                                color = Color.Black,
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                )
                Button(
                    onClick = {
                        buscando = true
                        mostrandoMensajeNoEncontrado = false // Reinicia el mensaje al iniciar una nueva búsqueda
                        buscarClienteEnFirebase(searchText) { resultado ->
                            cliente = resultado
                            buscando = false
                            if (resultado == null) {
                                mostrandoMensajeNoEncontrado = true // Muestra el mensaje solo si no hay resultado
                            }
                        }
                    },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Buscar")
                }
            }

            // Estado de búsqueda o resultado
            if (buscando) {
                Text("Buscando...", color = Color.Gray)
            } else if (cliente != null) {
                // Mostrar cliente encontrado
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                        .padding(horizontal = 8.dp, vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Gray, CircleShape)
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        // Mostrar nombre del cliente
                        Text(
                            text = cliente?.get("nombre") ?: "Nombre no disponible", // Usar .get() para acceder al valor
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        // Mostrar ciudad del cliente
                        Text(
                            text = "Ciudad: ${cliente?.get("Ciudad") ?: "No especificada"}", // Usar .get() para acceder al valor
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        // Mostrar ciudad del cliente
                        Text(
                            text = "DNI: ${cliente?.get("DNI") ?: "No especificada"}", // Usar .get() para acceder al valor
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    IconButton(onClick = { /* Acción de editar */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "Editar",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = { /* Acción de borrar */ }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_delete),
                            contentDescription = "Borrar",
                            tint = Color.Black
                        )
                    }
                }
            } else if (mostrandoMensajeNoEncontrado) {
                // Mostrar mensaje "Cliente no encontrado" solo si se buscó y no se encontró
                Text("Cliente no encontrado", color = Color.Red)
            }
        }
    }
}


// Función para buscar en Firebase
fun buscarClienteEnFirebase(nombre: String, onResult: (Map<String, String>?) -> Unit) {
    val db = Firebase.firestore
    db.collection("clientes")
        .whereEqualTo("nombre", nombre)
        .get()
        .addOnSuccessListener { result ->
            if (result.documents.isNotEmpty()) {
                val clienteData = result.documents[0].data as Map<String, String>
                onResult(clienteData)
            } else {
                onResult(null)
            }
        }
        .addOnFailureListener {
            onResult(null)
        }
}

@Composable
fun BottomBuscarClientes(navController: NavHostController) {
        NavigationBar(
            containerColor = Color.White
        ) {
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.buscarazul),
                        contentDescription = "Inicio",
                        tint = Color.Unspecified
                    )
                },
                selected = false, // Marca "Inicio" como seleccionado
                onClick = { navController.navigate("pantallaBuscarClientes") }
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.calendarioicono),
                        contentDescription = "Buscar"
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

