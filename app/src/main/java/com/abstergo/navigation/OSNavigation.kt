package com.abstergo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abstergo.ui.boot.BootScreen
import com.abstergo.ui.lock.LockScreen
import com.abstergo.ui.desktop.DesktopScreen

sealed class Screen(val route: String) {
    data object Boot : Screen("boot")
    data object Lock : Screen("lock")
    data object Desktop : Screen("desktop")
}

@Composable
fun OSNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Boot.route
    ) {
        composable(Screen.Boot.route) {
            BootScreen(
                onBootComplete = {
                    navController.navigate(Screen.Lock.route) {
                        popUpTo(Screen.Boot.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Lock.route) {
            LockScreen(
                onUnlock = {
                    navController.navigate(Screen.Desktop.route) {
                        popUpTo(Screen.Lock.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Desktop.route) {
            DesktopScreen(
                onLockScreen = {
                    navController.navigate(Screen.Lock.route) {
                        popUpTo(Screen.Desktop.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
