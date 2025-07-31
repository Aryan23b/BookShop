package com.example.libbook.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.libbook.screens.admin.AddStockScreen
import com.example.libbook.screens.admin.StockLevelsScreen
import com.example.libbook.screens.common.LoginScreen
import com.example.libbook.screens.users.BarcodeScannerScreen
import com.example.libbook.screens.users.CheckoutScreen
import com.example.libbook.screens.users.HomeScreen
import com.example.libbook.screens.users.OrderDetailScreen
import com.example.libbook.screens.users.OrderHistoryScreen
import com.example.libbook.screens.users.ShoppingCartScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(route = Screen.AdminStockLevels.route) {
            StockLevelsScreen(navController = navController)
        }

        composable(route = Screen.AddStock.route) {
            AddStockScreen(navController = navController)
        }

        // Customer Routes
        composable(
            route = Screen.CustomerHome.route,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            if (username != null) {
                HomeScreen(navController = navController, username = username)
            }
        }

        composable(
            route = Screen.ShoppingCart.route,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            if (username != null) {
                ShoppingCartScreen(navController = navController, username = username)
            }
        }

        composable(
            route = Screen.Checkout.route,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            if (username != null) {
                CheckoutScreen(navController = navController, username = username)
            }
        }

        composable(
            route = Screen.OrderHistory.route,
            arguments = listOf(navArgument("username") { type = NavType.StringType })
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username")
            if (username != null) {
                OrderHistoryScreen(navController = navController, username = username)
            }
        }

        composable(
            route = Screen.OrderDetail.route,
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getLong("orderId")
            if (orderId != null) {
                OrderDetailScreen(navController = navController, orderId = orderId)
            }
        }

        composable(route = Screen.BarcodeScanner.route) {
            BarcodeScannerScreen(navController = navController)
        }
    }
}
 