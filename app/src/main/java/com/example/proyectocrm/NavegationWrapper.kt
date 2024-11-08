package com.example.proyectocrm

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.proyectocrm.screens.PantallaCalendario
import com.example.proyectocrm.screens.PantallaLogin

@Composable
fun NavigationWrapper (navHostController: NavHostController) {

    NavHost(navController = navHostController, startDestination = "pantallaLogin") {
        composable ("pantallaLogin") {PantallaLogin(navHostController)}
        composable("PantallaCalendario") { PantallaCalendario(navHostController) }
        //composable("PantallaBuscarClientes") { PantallaBuscarClientes(navHostController) }
       // composable("PantallaMiPerfil") { PantallaMiPerfil(navHostController) }
        }

    }

