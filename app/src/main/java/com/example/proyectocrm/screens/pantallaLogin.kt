package com.example.proyectocrm.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.proyectocrm.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLogin(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()

    // Variables para los campos de entrada
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val message = remember { mutableStateOf("") }
    val passwordVisibility = remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(color = Color(0xFFF4F6F9)) // Fondo neutro y profesional
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_empresa),
            contentDescription = "Logo de la Empresa",
            modifier = Modifier
                .size(170.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Bienvenido al CRM",
            color = Color(0xFF34495E),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Campo de correo electrónico
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textStyle = TextStyle(color = Color(0xFF2C3E50)),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
        )

        // Campo de contraseña
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textStyle = TextStyle(color = Color(0xFF2C3E50)),
            visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisibility.value)
                    painterResource(id = android.R.drawable.ic_menu_close_clear_cancel)
                else
                    painterResource(id = android.R.drawable.ic_menu_view)

                IconButton(onClick = {
                    passwordVisibility.value = !passwordVisibility.value
                }) {
                    Icon(
                        painter = image,
                        contentDescription = "Toggle password visibility",
                        tint = Color(0xFFBDC3C7)
                    )
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Botón de inicio de sesión
        Button(
            onClick = {
                loginUser(auth, email.value, password.value, navController, message)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0756FF)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Iniciar Sesión", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón de registro
        Button(
            onClick = {
                navController.navigate("pantallaRegistro")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0756FF)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Registrarse", color = Color.White, fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mensajes de estado
        Text(
            text = message.value,
            color = Color(0xFF7F8C8D),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

// Función para iniciar sesión
fun loginUser(
    auth: FirebaseAuth,
    email: String,
    password: String,
    navController: NavHostController,
    message: MutableState<String>
) {
    if (email.isNotBlank() && password.isNotBlank()) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    message.value = "Inicio de sesión exitoso"
                    navController.navigate("pantallaCalendario")
                } else {
                    message.value = "Error: ${task.exception?.message}"
                }
            }
    } else {
        message.value = "Por favor, completa todos los campos"
    }
}
