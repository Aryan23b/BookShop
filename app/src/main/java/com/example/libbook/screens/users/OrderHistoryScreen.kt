package com.example.libbook.screens.users


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.libbook.data.models.Order
import com.example.libbook.navigation.Screen
import com.example.libbook.viewmodels.OrderHistoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavController,
    username: String,
    orderHistoryViewModel: OrderHistoryViewModel = viewModel()
) {
    val orders by orderHistoryViewModel.orders.collectAsState()

    LaunchedEffect(username) {
        orderHistoryViewModel.loadUserOrders(username)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFF0A1A41)
            )
    ) {

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF494E8A), // Using a theme color
                        titleContentColor = Color.White                  // Color for the title
                    ),
                    title = { Text("Your Orders", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }
                )
            }
        ) { paddingValues ->
            if (orders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("You have no past orders.", color = Color.White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(orders) { order ->
                        OrderHistoryItem(
                            order = order,
                            onClick = {
                                navController.navigate(Screen.OrderDetail.createRoute(order.orderId))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryItem(order: Order, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF9BA9FF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Order #${order.orderId}", fontWeight = FontWeight.Bold)
            Text("Date: ${formatDate(order.orderDate)}")
            Text("Total: ₹${"%.2f".format(order.totalAmount)}")
        }
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}