package com.example.myapplication.main

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.screen.gmaps.MapScreen
import com.example.myapplication.presentation.screen.login.LoginScreen

@Composable
fun InfiniteApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") { LoginScreen(navController) }
        composable("home") { MapScreen() }
    }
}