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
import com.google.firebase.firestore.FirebaseFirestore

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
    var clienteId by remember { mutableStateOf<String?>(null) } // ID del cliente
    var buscando by remember { mutableStateOf(false) } // Para mostrar estado de búsqueda
    var mostrandoMensajeNoEncontrado by remember { mutableStateOf(false) } // Controla el mensaje "Cliente no encontrado"
    var mostrarFormulario by remember { mutableStateOf(false) }
    var mostrarFormularioEditar by remember { mutableStateOf(false) } // Estado para mostrar el formulario de edición
    var nombre by remember { mutableStateOf("") }
    var gmail by remember { mutableStateOf("") }
    var DNI by remember { mutableStateOf("") }
    var Ciudad by remember { mutableStateOf("") }

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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    mostrarFormulario = true // Mostrar el formulario
                },
                containerColor = Color(0xFF0756FF), // Color de fondo
                contentColor = Color.White // Color del contenido
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.iconosumar), // Icono "+"
                    contentDescription = "Agregar nuevo cliente",
                    modifier = Modifier.size(24.dp) // Tamaño del icono
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End // Posiciona el FAB en la parte inferior derecha
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
                    tint = Color.Black,
                    modifier = Modifier.size(35.dp)
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
                                text = "Buscar cliente ",
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
                        mostrandoMensajeNoEncontrado =
                            false // Reinicia el mensaje al iniciar una nueva búsqueda
                        buscarClienteEnFirebase(searchText) { resultado, id ->
                            cliente = resultado
                            clienteId = id
                            buscando = false
                            if (resultado == null) {
                                mostrandoMensajeNoEncontrado =
                                    true // Muestra el mensaje solo si no hay resultado
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0756FF)),
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
                            text = cliente?.get("nombre")
                                ?: "Nombre no disponible", // Usar .get() para acceder al valor
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
                        // Mostrar DNI del cliente
                        Text(
                            text = "DNI: ${cliente?.get("DNI") ?: "No especificado"}", // Usar .get() para acceder al valor
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                    IconButton(onClick = {
                        mostrarFormularioEditar = true // Activar el formulario de edición
                        // Rellenar el formulario con los datos actuales
                        nombre = cliente?.get("nombre") ?: ""
                        gmail = cliente?.get("Gmail") ?: ""
                        DNI = cliente?.get("DNI") ?: ""
                        Ciudad = cliente?.get("Ciudad") ?: ""
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_edit),
                            contentDescription = "Editar",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = {
                        clienteId?.let {
                            eliminarCliente(it) {
                                // Actualizar el estado después de eliminar el cliente
                                cliente = null
                                clienteId = null
                                mostrandoMensajeNoEncontrado = false
                            }
                        }
                    }) {
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

    // Formulario para editar el cliente
    if (mostrarFormularioEditar) {
        AlertDialog(
            onDismissRequest = {
                mostrarFormularioEditar = false
            }, // Cierra el formulario al hacer clic fuera
            title = {
                Text(
                    text = "Editar Cliente",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    campoTexto("Gmail", gmail, { gmail = it })
                    campoTexto("Ciudad", Ciudad, { Ciudad = it })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clienteId?.let {
                            actualizarCliente(it, nombre, gmail, DNI, Ciudad) {
                                mostrarFormularioEditar = false // Cierra el formulario tras actualizar
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0756FF))
                ) {
                    Text("Guardar Cambios")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarFormularioEditar = false } // Cierra el formulario
                ) {
                    Text("Cancelar")
                }
            },
            modifier = Modifier.fillMaxWidth(0.9f) // Controla el tamaño del diálogo
        )
    }
    if (mostrarFormulario) {
        AlertDialog(
            onDismissRequest = {
                mostrarFormulario = false
            }, // Cierra el formulario al hacer clic fuera
            title = {
                Text(
                    text = "Formulario de Clientes",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Campos del formulario
                    campoTexto("Nombre", nombre, { nombre = it })
                    campoTexto("Gmail", gmail, { gmail = it })
                    campoTexto("DNI", DNI, { DNI = it })
                    campoTexto("Ciudad", Ciudad, { Ciudad = it })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        guardarClientes(nombre, gmail, DNI, Ciudad) {
                            mostrarFormulario = false // Cierra el formulario tras guardar
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0756FF))
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarFormulario = false } // Cierra el formulario
                ) {
                    Text("Cancelar")
                }
            },
            modifier = Modifier.fillMaxWidth(0.9f) // Controla el tamaño del diálogo
        )
    }
}

fun actualizarCliente(clienteId: String, nombre: String, gmail: String, DNI: String, Ciudad: String, function: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val clienteActualizado = mapOf(
        "nombre" to nombre,
        "Gmail" to gmail,
        "DNI" to DNI,
        "Ciudad" to Ciudad
    )

    db.collection("clientes").document(clienteId).update(clienteActualizado)
        .addOnSuccessListener {
            println("Cliente actualizado correctamente")
            function() // Llamar a la función para cerrar el formulario
        }
        .addOnFailureListener { e ->
            println("Error al actualizar cliente: ${e.localizedMessage}")
            }

        }

fun buscarClienteEnFirebase(nombre: String, onResult: (cliente: Map<String, String>?, id: String?) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("clientes")
        .whereEqualTo("nombre", nombre)
        .get()
        .addOnSuccessListener { result ->
            if (result.documents.isNotEmpty()) {
                val clienteData = result.documents[0].data as Map<String, String>
                val clienteId = result.documents[0].id // Obtiene el ID del cliente
                onResult(clienteData, clienteId)
            } else {
                onResult(null, null)
            }
        }
        .addOnFailureListener {
            onResult(null, null)
        }
}

fun eliminarCliente(clienteId: String, onDeleteSuccess: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("clientes").document(clienteId)
        .delete()
        .addOnSuccessListener {
            // Cliente eliminado correctamente
            println("Cliente eliminado")
            onDeleteSuccess() // Llamar a la función para actualizar la UI después de la eliminación
        }
        .addOnFailureListener { e ->
            // Error al eliminar
            println("Error al eliminar cliente: $e")
        }

}
    fun guardarClientes(
        nombre: String,
        gmail: String,
        DNI: String,
        Ciudad: String,
        function: () -> Unit
    ) {
        // Validar que los campos obligatorios estén completos
        if (nombre.isNotEmpty() && gmail.isNotEmpty() && DNI.isNotEmpty() && Ciudad.isNotEmpty()) {
            // Estructura del cliente que se guardará en Firestore
            val cliente = mapOf(
                "nombre" to nombre,
                "Gmail" to gmail,
                "DNI" to DNI,
                "Ciudad" to Ciudad
            )

            // Referencia a la base de datos
            val db = FirebaseFirestore.getInstance()
            val coleccion = "clientes"

            // Guardar cliente en la colección usando el DNI como documento
            db.collection(coleccion).document(DNI).set(cliente)
                .addOnSuccessListener {
                    println("Cliente guardado correctamente")

                }
                .addOnFailureListener { e ->
                    println("Error al guardar cliente: ${e.localizedMessage}")
                }
        } else {
            // Mostrar mensaje en consola si faltan campos
            println("Por favor, complete todos los campos obligatorios")
        }
    }
@Composable
fun campoTexto(
    etiqueta: String,
    valor: String,
    onValorChange: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Text(
            text = etiqueta,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.align(Alignment.Start)
        )
        BasicTextField(
            value = valor,
            onValueChange = onValorChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = MaterialTheme.shapes.small)
                .padding(8.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (valor.isEmpty()) {
                    Text(
                        text = "Escribe $etiqueta",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                innerTextField()
            }
        )
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
