package com.example.libbook.navigation



sealed class Screen(val route: String) {
    data object Login : Screen("login_screen")
    data object AdminStockLevels : Screen("admin_stock_levels_screen")
    data object AddStock : Screen("add_stock_screen")
    // We will add customer screens like Home, Cart, etc. later

    data object CustomerHome : Screen("customer_home_screen/{username}") {
        fun createRoute(username: String) = "customer_home_screen/$username"
    }

    data object ShoppingCart : Screen("shopping_cart_screen/{username}") {
        fun createRoute(username: String) = "shopping_cart_screen/$username"
    }

    data object Checkout : Screen("checkout_screen/{username}") {
        fun createRoute(username: String) = "checkout_screen/$username"
    }

    data object OrderHistory : Screen("order_history_screen/{username}") {
        fun createRoute(username: String) = "order_history_screen/$username"
    }
    data object OrderDetail : Screen("order_detail_screen/{orderId}") {
        fun createRoute(orderId: Long) = "order_detail_screen/$orderId"
    }

    data object BarcodeScanner : Screen("barcode_scanner_screen")
}