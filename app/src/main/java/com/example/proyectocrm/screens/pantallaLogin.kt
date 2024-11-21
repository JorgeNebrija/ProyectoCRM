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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.navigation.NavHostController
import com.example.proyectocrm.R
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLogin(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()

    // Variables para los campos de entrada
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val message = remember { mutableStateOf("") }
    val passwordVisibility = remember { mutableStateOf(false) }

    // Usamos un color de fondo más neutro y elegante
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(color = Color(0xFFF4F6F9)) // Fondo claro y profesional
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_empresa), // Aquí coloca el logo en los recursos
            contentDescription = "Logo de la Empresa",
            modifier = Modifier
                .size(170.dp) // Ajusta el tamaño según lo necesites
                .padding(bottom = 16.dp) // Espaciado entre el logo y el texto
        )
        // Título con un estilo más formal
        Text(
            text = "Bienvenido al CRM",
            color = Color(0xFF34495E), // Color más sobrio y serio
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Campo de correo electrónico con un estilo más profesional
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textStyle = TextStyle(color = Color(0xFF2C3E50)), // Texto oscuro y elegante
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF3498DB),
                unfocusedBorderColor = Color(0xFFBDC3C7),
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { /* Acción de siguiente */ }
            )
        )

        // Campo de contraseña con visibilidad de contraseña
        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Contraseña") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textStyle = TextStyle(color = Color(0xFF2C3E50)),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color(0xFF3498DB),
                unfocusedBorderColor = Color(0xFFBDC3C7),
            ),
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
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { /* Acción al completar */ }
    )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Botón de inicio de sesión con un estilo más sobrio
        Button(
            onClick = {
                loginUser(auth, email.value, password.value, navController, message)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0756FF)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(12.dp)
        ) {
            Text(
                text = "Iniciar Sesión",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón de registro con un color ligeramente distinto para diferenciarlos
        Button(
            onClick = {
                registerUser(auth, email.value, password.value, message)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0756FF)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentPadding = PaddingValues(12.dp)
        ) {
            Text(
                text = "Registrarse",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Mostrar mensajes de estado con color neutro para mantener el tono formal
        Text(
            text = message.value,
            color = Color(0xFF7F8C8D),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

// Función para iniciar sesión
fun loginUser(auth: FirebaseAuth, email: String, password: String, navController: NavHostController, message: MutableState<String>) {
    if (email.isNotBlank() && password.isNotBlank()) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    message.value = "Inicio de sesión exitoso"
                    navController.navigate("pantallaConfClientes")
                } else {
                    message.value = "Error: ${task.exception?.message}"
                }
            }
    } else {
        message.value = "Por favor, completa todos los campos"
    }
}

// Función para el registro
private fun registerUser(auth: FirebaseAuth, email: String, password: String, message: MutableState<String>) {
    if (email.isNotBlank() && password.isNotBlank()) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    message.value = "Registro exitoso"
                } else {
                    message.value = "Error: ${task.exception?.message}"
                }
            }
    } else {
        message.value = "Por favor, completa todos los campos"
    }
}
