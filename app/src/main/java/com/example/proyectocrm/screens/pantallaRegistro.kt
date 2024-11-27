package com.example.proyectocrm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistro(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Variables para los campos de entrada
    val gmail = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val ciudad = remember { mutableStateOf("") }
    val rol = remember { mutableStateOf("") }
    val dni = remember { mutableStateOf("") }
    val telefono = remember { mutableStateOf("") } // Nuevo campo para teléfono
    val message = remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(color = Color(0xFFF4F6F9))
    ) {
        Text(
            text = "Registro de Usuario",
            color = Color(0xFF34495E),
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Campos de entrada
        OutlinedTextField(
            value = gmail.value,
            onValueChange = { gmail.value = it },
            label = { Text("Correo Electrónico (Gmail)") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        )

        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        )

        OutlinedTextField(
            value = telefono.value,
            onValueChange = { telefono.value = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        )

        OutlinedTextField(
            value = ciudad.value,
            onValueChange = { ciudad.value = it },
            label = { Text("Ciudad") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        )

        OutlinedTextField(
            value = rol.value,
            onValueChange = { rol.value = it },
            label = { Text("Rol") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        )

        OutlinedTextField(
            value = dni.value,
            onValueChange = { dni.value = it },
            label = { Text("DNI") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Botón de registro
        Button(
            onClick = {
                registerUser(auth, db, gmail.value, password.value, telefono.value, ciudad.value, rol.value, dni.value, message)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0756FF)),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text(text = "Registrarse", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mensajes de estado
        Text(
            text = message.value,
            color = Color(0xFF7F8C8D),
            fontSize = 14.sp
        )

        // Botón para volver al inicio de sesión
        TextButton(onClick = { navController.popBackStack() }) {
            Text(text = "Volver al inicio de sesión", color = Color(0xFF3498DB))
        }
    }
}

// Función para registrar un usuario con datos adicionales
fun registerUser(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    gmail: String,
    password: String,
    telefono: String,  // Campo teléfono añadido
    ciudad: String,
    rol: String,
    dni: String,
    message: MutableState<String>
) {
    if (gmail.isNotBlank() && password.isNotBlank() && telefono.isNotBlank() && ciudad.isNotBlank() && rol.isNotBlank() && dni.isNotBlank()) {
        auth.createUserWithEmailAndPassword(gmail, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val userData = hashMapOf(
                        "gmail" to gmail,
                        "telefono" to telefono,  // Guardamos el teléfono
                        "ciudad" to ciudad,
                        "rol" to rol,
                        "dni" to dni
                    )

                    user?.let {
                        db.collection("clientes").document(it.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                message.value = "Registro exitoso"
                            }
                            .addOnFailureListener { e ->
                                message.value = "Error al guardar datos en Firestore: ${e.message}"
                            }
                    }
                } else {
                    message.value = "Error: ${task.exception?.message}"
                }
            }
    } else {
        message.value = "Por favor, completa todos los campos"
    }
}
