package com.example.proyectocrm

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectocrm.screens.PantallaBuscarClientes
import com.example.proyectocrm.screens.PantallaCalendario
import com.example.proyectocrm.screens.PantallaLogin
import com.example.proyectocrm.screens.PantallaMiPerfil
import com.example.proyectocrm.screens.PantallaRegistro

@Composable
fun NavigationWrapper (navHostController: NavHostController) {

    NavHost(navController = navHostController, startDestination = "pantallaLogin") {
        composable ("pantallaLogin") {PantallaLogin(navHostController)}
        composable ("pantallaRegistro") {PantallaRegistro(navHostController)}
        composable("PantallaCalendario") { PantallaCalendario(navHostController) }
       composable("PantallaMiPerfil") { PantallaMiPerfil(navHostController) }
composable("pantallaBuscarClientes") { PantallaBuscarClientes(navHostController)  }
        }

    }

