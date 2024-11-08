package com.example.proyectocrm.screens



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLogin(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance() // Inicializa la instancia de FirebaseAuth

    // Variables
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val message = remember { mutableStateOf("") }
    val passwordVisibility = remember { mutableStateOf(false) }

    //
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
    ) {

        Text(
            text = "Login y Registro",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.size(24.dp))

        //
        OutlinedTextField(
            value = email.value,
            onValueChange = { email.value = it }, // Actualiza el valor del estado
            label = { Text("Correo Electrónico", color = Color.White) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textStyle = TextStyle(
                color = Color.White // Cambia el color del texto dentro del campo
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Blue, // Cambia el color del borde cuando está enfocado
                unfocusedBorderColor = Color.Gray, // Cambia el color del borde cuando no está enfocado

            )
        )


        Spacer(modifier = Modifier.size(16.dp))


        OutlinedTextField(
            value = password.value,
            onValueChange = { password.value = it }, // Actualiza el valor del estado
            label = { Text("Contraseña", color = Color.White) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textStyle = TextStyle(
                color = Color.White // Cambia el color del texto dentro del campo
            ),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = Color.Blue, // Cambia el color del borde cuando está enfocado
                unfocusedBorderColor = Color.Gray, // Cambia el color del borde cuando no está enfocado

            ),
            visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(), // Controla la visibilidad de la contraseña
            trailingIcon = {
                val image = if (passwordVisibility.value)
                    painterResource(id = android.R.drawable.ic_menu_close_clear_cancel) // Icono para ocultar la contraseña
                else
                    painterResource(id = android.R.drawable.ic_menu_view) // Icono para mostrar la contraseña

                IconButton(onClick = {
                    passwordVisibility.value = !passwordVisibility.value // Cambia la visibilidad de la contraseña
                }) {
                    Icon(
                        painter = image,
                        contentDescription = "Toggle password visibility",
                        tint = Color.White // Cambia el color del icono de visibilidad de la contraseña
                    )
                }
            }
        )


        Spacer(modifier = Modifier.size(16.dp))

        // Botón para iniciar sesión
        Button(
            onClick = {
                loginUser(auth, email.value, password.value, navController, message) // Llama a la función de inicio de sesión
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            Text(
                text = "Iniciar Sesión",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.size(8.dp))


        Button(
            onClick = {
                registerUser(auth, email.value, password.value, message) // Llama a la función de registro
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(8.dp)
        ) {
            Text(
                text = "Registrarse",
                color = Color.White,
                fontSize = 16.sp,
                style = TextStyle.Default
            )
        }

        Spacer(modifier = Modifier.size(16.dp))

        // Muestra mensajes de estado (bien o error)
        Text(
            text = message.value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// Función para el inicio de sesión
fun loginUser(auth: FirebaseAuth, email: String, password: String, navController: NavController, message: MutableState<String>) {
    if (email.isNotBlank() && password.isNotBlank()) { // Verifica que email y password no estén vacíos
        auth.signInWithEmailAndPassword(email, password) // Llama a Firebase para iniciar sesión
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    message.value = "Inicio de sesión exitoso" // Actualiza el mensaje
                    navController.navigate("PantallaCalendario ") { // Navega a la pantalla de menú
                    }
                } else {
                    message.value = "Error: ${task.exception?.message}"
                }
            }
    } else {
        message.value = "Por favor, completa todos los campos"
    }
}

// Función para manejar el registro
private fun registerUser(auth: FirebaseAuth, email: String, password: String, message: MutableState<String>) {
    if (email.isNotBlank() && password.isNotBlank()) { // Verifica que los campos no estén vacíos
        auth.createUserWithEmailAndPassword(email, password) // Llama a Firebase para registrar al usuario
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    message.value = "Registro exitoso" // Actualiza el mensaje
                } else {
                    message.value = "Error: ${task.exception?.message}" // Muestra el mensaje de error
                }
            }
    } else {
        message.value = "Por favor, completa todos los campos" // Muestra un mensaje si los campos están vacíos
    }
}