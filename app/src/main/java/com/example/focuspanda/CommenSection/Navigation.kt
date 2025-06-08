package com.example.focuspanda.CommenSection

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.*
import com.example.app.settings.SettingsScreen
import com.example.focuspanda.Model.NavIterm
import com.example.focuspanda.Screens.DetailScreen
import com.example.focuspanda.Screens.FeaturedDrinksPage
import com.example.focuspanda.Screens.FlashcardScreen
import com.example.focuspanda.Screens.LoginScreen
import com.example.focuspanda.Screens.MainScreen
import com.example.focuspanda.Screens.PomodoroTimerScreen
import com.example.focuspanda.Screens.QuotesScreen
import com.example.focuspanda.Screens.ToDoListScreen
import com.example.focuspanda.Screens.UserProfileScreen
import com.example.focuspanda.Screens.QuotesScreen

import com.example.focuspanda.SplashScreen


@Composable
fun BottomNavigationScreen(navController: NavController) {
    val bottomNavController = rememberNavController()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(bottomNavController) }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("pomodoro") { PomodoroTimerScreen(bottomNavController) }
            composable("todo") { ToDoListScreen(bottomNavController) }
            composable("dashboard") { MainScreen(bottomNavController) }
            composable("profile") { UserProfileScreen(bottomNavController) }
            composable("flashCards") { FlashcardScreen(bottomNavController) }
            composable("quotes") { QuotesScreen(bottomNavController) }

            composable("settings") { SettingsScreen(bottomNavController) }

            composable("FeaturedDrinksPage") { FeaturedDrinksPage(bottomNavController) }

            // ✅ Corrected indentation of DetailScreen
            composable("details/{feature}") { backStackEntry ->
                val feature = backStackEntry.arguments?.getString("feature")
                DetailScreen(navController = bottomNavController, feature = feature)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navItems = listOf(
        NavIterm("pomodoro", Icons.Default.Star),
        NavIterm("todo", Icons.Default.DateRange),
        NavIterm("dashboard", Icons.Default.List),
        NavIterm("profile", Icons.Default.Person)
    )

    val currentDestination = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        navItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentDestination == navItem.label,
                onClick = {
                    navController.navigate(navItem.label) {
                        popUpTo("dashboard") { inclusive = false }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(navItem.icon, contentDescription = navItem.label) },
                label = { Text(navItem.label.replaceFirstChar { it.uppercase() }) }
            )
        }
    }
}
@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash" // adds Splash as the first screen
    ) {
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("main") { BottomNavigationScreen(navController) } // Bottom navigation
    }
}
