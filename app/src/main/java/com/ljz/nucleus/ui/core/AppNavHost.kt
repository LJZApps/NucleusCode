package com.ljz.nucleus.ui.core

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ljz.nucleus.start.EmailCheck
import com.ljz.nucleus.start.LoginWithEmail
import com.ljz.nucleus.start.RegisterAccount
import com.ljz.nucleus.start.RegisterHome
import com.ljz.nucleus.start.ResetPassword

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "checkEmail"
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable("checkEmail") {
            EmailCheck(
                navController = navController
            )
        }
        composable(
            "registerWithEmail?email={email}",
            arguments = listOf(navArgument("email") { defaultValue = "null" })
        ) { backStateEntry ->
            backStateEntry.arguments?.getString("email")?.let {
                RegisterHome(
                    navController = navController,
                    email = it
                )
            }

            BackHandler(true) {
                // Nothing
            }
        }
        composable("registerAccountInformation") {
            RegisterAccount(navController = navController)

            BackHandler(true) {
                // Nothing
            }
        }
        composable("resetPassword") {
            ResetPassword(navController = navController)

            BackHandler(true) {
                // Nothing
            }
        }
        composable(
            "loginWithEmail?email={email}",
            arguments = listOf(
                navArgument("email") { defaultValue = "null" }
            )
        ) { backStateEntry ->
            backStateEntry.arguments?.getString("email")?.let {
                LoginWithEmail(
                    navToEmailCheck = { navController.navigate("checkEmail") },
                    navController = navController,
                    email = it
                )
            }

            BackHandler(true) {
                // Nothing
            }
        }
    }
}