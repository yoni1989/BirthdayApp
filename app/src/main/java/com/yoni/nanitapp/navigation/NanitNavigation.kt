package com.yoni.nanitapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.yoni.nanitapp.presentation.birthday.BirthdayRoute
import com.yoni.nanitapp.presentation.connection.ConnectionRoute

const val CONNECTION = "connection"
const val BIRTHDAY = "birthday"

@Composable
fun BirthdayNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = CONNECTION
    ) {
        composable(CONNECTION) {
            ConnectionRoute(
                onNavigateToBirthday = {
                    navController.navigate(BIRTHDAY) {
                        popUpTo(CONNECTION) { inclusive = true }
                    }
                }
            )
        }

        composable(BIRTHDAY) {
            BirthdayRoute()
        }
    }
}