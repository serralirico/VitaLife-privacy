package com.serranen.viajeia.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.serranen.viajeia.di.AppContainer
import com.serranen.viajeia.ui.chat.ChatScreen
import com.serranen.viajeia.ui.diary.TripDetailScreen
import com.serranen.viajeia.ui.home.HomeScreen
import com.serranen.viajeia.ui.planner.PlannerScreen
import com.serranen.viajeia.ui.settings.SettingsScreen

sealed class Dest(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Dest("home", "Viajes", Icons.Filled.Map)
    data object Planner : Dest("planner", "Planificar", Icons.Filled.Explore)
    data object Chat : Dest("chat", "Guía IA", Icons.AutoMirrored.Filled.Chat)
    data object Settings : Dest("settings", "Ajustes", Icons.Filled.Settings)
}

private val bottomItems = listOf(Dest.Home, Dest.Planner, Dest.Chat, Dest.Settings)

@Composable
fun ViajeIaApp(container: AppContainer) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        bottomBar = {
            // Oculta la barra en pantallas de detalle.
            if (currentRoute == null || bottomItems.any { it.route == currentRoute }) {
                NavigationBar {
                    val current = backStack?.destination
                    bottomItems.forEach { dest ->
                        NavigationBarItem(
                            selected = current?.hierarchy?.any { it.route == dest.route } == true,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(dest.icon, contentDescription = dest.label) },
                            label = { Text(dest.label) },
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Dest.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Dest.Home.route) {
                HomeScreen(
                    container = container,
                    onOpenTrip = { id -> navController.navigate("trip/$id") },
                    onPlan = { navController.navigate(Dest.Planner.route) },
                )
            }
            composable(Dest.Planner.route) {
                PlannerScreen(
                    container = container,
                    onSaved = { id -> navController.navigate("trip/$id") },
                )
            }
            composable(Dest.Chat.route) { ChatScreen(container = container) }
            composable(Dest.Settings.route) { SettingsScreen(container = container) }
            composable("trip/{tripId}") { entry ->
                val tripId = entry.arguments?.getString("tripId")?.toLongOrNull() ?: 0L
                TripDetailScreen(
                    container = container,
                    tripId = tripId,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
