package com.example.proyectocrm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.proyectocrm.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PantallaConfClientes(navController: NavHostController) {
    ScaffoldConfClientes(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldConfClientes(navController: NavHostController) {
    // Estados para los campos del formulario
    var nombre by remember { mutableStateOf("") }
    var gmail by remember { mutableStateOf("") }
    var DNI by remember { mutableStateOf("") }
    var Ciudad by remember { mutableStateOf("") }

    // Obteniendo el Gmail del usuario autenticado en Firebase
    val auth = FirebaseAuth.getInstance()
    val usuario = auth.currentUser

    // Usamos LaunchedEffect para evitar la asignación repetida del Gmail
    LaunchedEffect(usuario) {
        gmail = usuario?.email ?: "No autenticado" // Autocompleta Gmail o muestra "No autenticado"
    }

    // Lista de campos
    val campos = listOf(
        Campo("Nombre", nombre, { nombre = it }),
        Campo("Gmail", gmail, { gmail = it }, soloLectura = true),
        Campo("DNI", DNI, { DNI = it }),
        Campo("Ciudad", Ciudad, { Ciudad = it })
    )

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        "Formulario de Clientes",
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
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(color = Color(0xFFE4E4E4))
                .padding(16.dp)
                .padding(top=16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Iteramos sobre la lista de campos para generar los campos de texto
            campos.forEach { campo ->
                CampoTexto(campo.etiqueta, campo.valor, campo.setValor, soloLectura = campo.soloLectura)
            }

            // Botón para guardar datos y navegar a PantallaBuscarClientes
            Button(
                onClick = {
                    guardarCliente(nombre, gmail, DNI, Ciudad) {
                        // Navegar a la pantalla PantallaBuscarClientes una vez que el cliente se guarde
                        navController.navigate("pantallaCalendario")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0756FF))
            ) {
                Text("Guardar")
            }
            // Texto debajo del botón
            Text(
                text = "Por favor, complete este formulario y, al hacer clic en 'Guardar', acepta nuestras condiciones de aplicación y se registrará como nuevo cliente de JSTUDIO.",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

fun guardarCliente(nombre: String, gmail: String, DNI: String, Ciudad: String, onSuccess: () -> Unit) {
    if (nombre.isNotEmpty() && DNI.isNotEmpty() && Ciudad.isNotEmpty()) {
        // Aquí va el código para guardar los datos en Firestore
        val cliente = mapOf(
            "nombre" to nombre,
            "Gmail" to gmail,
            "DNI" to DNI,
            "Ciudad" to Ciudad
        )

        val db = FirebaseFirestore.getInstance()
        val coleccion = "clientes"

        db.collection(coleccion).document(DNI).set(cliente)
            .addOnSuccessListener {
                println("Cliente guardado correctamente")
                onSuccess()  // Llamar la función onSuccess para navegar
            }
            .addOnFailureListener {
                println("Error al guardar cliente")
            }
    } else {
        println("Por favor, complete todos los campos")
    }
}

@Composable
fun CampoTexto(
    etiqueta: String,
    valor: String,
    onValueChange: (String) -> Unit,
    soloLectura: Boolean = false
) {
    Column {
        Text(
            text = etiqueta,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        BasicTextField(
            value = valor,
            onValueChange = onValueChange,  // Cambiamos aquí para actualizar el valor
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = MaterialTheme.shapes.small)
                .padding(8.dp),
            readOnly = soloLectura
        )
    }
}
