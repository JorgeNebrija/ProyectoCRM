package com.example.proyectocrm.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.proyectocrm.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Definir el color principal
private val colorPrincipal = Color(0xFFE4E4E4)

@Composable
fun PantallaMiPerfil(navController: NavHostController) {
    ScaffoldMiPerfil(navController)
}

@Composable
fun CambiarContrasenaDialog(
    onDismiss: () -> Unit,
    onPasswordChange: (String) -> Unit
) {
    val nuevaContrasena = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Cambiar Contraseña") },
        text = {
            Column {
                TextField(
                    value = nuevaContrasena.value,
                    onValueChange = { nuevaContrasena.value = it },
                    label = { Text("Nueva Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onPasswordChange(nuevaContrasena.value)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                ),
            ) {
                Text("Cambiar")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldMiPerfil(navController: NavHostController) {
    val mostrarDialogo = remember { mutableStateOf(false) }
    val datosUsuario = remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    // Cargar datos del usuario
    LaunchedEffect(Unit) {
        obtenerDatosUsuario { datos ->
            datosUsuario.value = datos
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        "Mi Perfil",
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
            BottomMiPerfil(navController)
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = colorPrincipal)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val gmail = datosUsuario.value["gmail"] ?: "No disponible"
            val telefono = datosUsuario.value["telefono"] ?: "No disponible"
            val ciudad = datosUsuario.value["ciudad"] ?: "No disponible"
            val rol = datosUsuario.value["rol"] ?: "No disponible"
            val dni = datosUsuario.value["dni"] ?: "No disponible"

            Row(
                modifier = Modifier
                    .height(70.dp)
                    .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.iconobuscar),
                    contentDescription = "Foto Default",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = gmail,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp)
                    .background(Color.LightGray, shape = MaterialTheme.shapes.small)
                    .fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Teléfono: $telefono", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Ciudad: $ciudad", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Rol: $rol", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("DNI: $dni", modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }
            }

            Button(
                onClick = { cerrarSesion(navController) },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(180.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Cerrar sesión")
            }

            Button(
                onClick = { mostrarDialogo.value = true },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(180.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Cambiar Contraseña")
            }

            if (mostrarDialogo.value) {
                CambiarContrasenaDialog(
                    onDismiss = { mostrarDialogo.value = false },
                    onPasswordChange = { nuevaContrasena ->
                        actualizarContrasena(nuevaContrasena)
                    }
                )
            }
        }
    }
}

fun actualizarContrasena(nuevaContrasena: String) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    if (currentUser != null) {
        currentUser.updatePassword(nuevaContrasena)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Contraseña actualizada exitosamente.")
                } else {
                    println("Error al actualizar la contraseña: ${task.exception?.message}")
                }
            }
    } else {
        println("No hay usuario autenticado para actualizar la contraseña.")
    }
}

fun cerrarSesion(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    auth.signOut()
    navController.navigate("pantallaLogin") {
        popUpTo("pantallaMiPerfil") { inclusive = true }
    }
}fun obtenerDatosUsuario(callback: (Map<String, String>) -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    if (currentUser != null) {
        val db = FirebaseFirestore.getInstance()

        // Aquí se obtiene el DNI del usuario actual, asumiendo que lo tienes guardado en algún lugar.
        val dni = currentUser.uid // Esto es solo un ejemplo, ajusta según la forma en que obtienes el DNI del usuario.
        db.collection("clientes").document(dni).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val datosUsuario = mapOf(
                        "gmail" to (document.getString("gmail") ?: "Correo no encontrado"),
                        "telefono" to (document.getString("telefono") ?: "Teléfono no encontrado"),
                        "ciudad" to (document.getString("ciudad") ?: "Ciudad no encontrada"),
                        "rol" to (document.getString("rol") ?: "Rol no encontrado"),
                        "dni" to (document.getString("dni") ?: "DNI no encontrado")
                    )
                    callback(datosUsuario)
                } else {
                    println("Documento no encontrado en Firebase.")
                    callback(emptyMap())
                }
            }
            .addOnFailureListener { exception ->
                println("Error al obtener los datos del usuario: ${exception.message}")
                callback(emptyMap())
            }

            .addOnFailureListener { exception ->
                println("Error al obtener los datos del usuario: ${exception.message}")
                callback(emptyMap())
            }
    } else {
        println("No hay usuario autenticado.")
        callback(emptyMap())
    }
}


@Composable
fun BottomMiPerfil(navController: NavHostController) {
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.iconobuscar),
                    contentDescription = "Inicio"
                )
            },
            selected = false,
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
                    painter = painterResource(id = R.drawable.buscarazul),
                    contentDescription = "Perfil",
                    tint = Color.Unspecified
                )
            },
            selected = false,
            onClick = { navController.navigate("pantallaMiPerfil") }
        )
    }
}
