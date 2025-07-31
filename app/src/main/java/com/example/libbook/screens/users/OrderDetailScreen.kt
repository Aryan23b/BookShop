package com.example.libbook.screens.users


import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.libbook.data.models.OrderDetailWithBook
import com.example.libbook.viewmodels.OrderHistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: Long,
    orderHistoryViewModel: OrderHistoryViewModel = viewModel()
) {
    val details by orderHistoryViewModel.selectedOrderDetails.collectAsState()
    val order by orderHistoryViewModel.selectedOrder.collectAsState() // Get the order state
    val context = LocalContext.current // Get the context for the share intent

    LaunchedEffect(orderId) {
        orderHistoryViewModel.loadOrderDetails(orderId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A1A41)
            )
    ) {

        Scaffold(containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF494E8A), // Using a theme color
                        titleContentColor = Color.White                  // Color for the title
                    ),
                    title = { Text("Order #${orderId}") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",
                                tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            // Build the text to share
                            val summary = buildOrderSummary(order, details)
                            // Trigger the share action
                            shareOrderDetails(context, summary)
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share Order",
                                tint = Color.White)
                        }
                    }

                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.padding(paddingValues),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(details) { detail ->
                    OrderDetailListItem(detail = detail)
                    Divider()
                }
            }
        }
    }
}

private fun buildOrderSummary(order: com.example.libbook.data.models.Order?, details: List<OrderDetailWithBook>): String {
    if (order == null) return "No order details found."

    val itemsText = details.joinToString("\n") {
        "- ${it.book.title} (Qty: ${it.orderDetail.quantity})"
    }

    return """
        My Book Shop Order #${order.orderId}
        
        Items:
        $itemsText
        
        Total: ₹${"%.2f".format(order.totalAmount)}
    """.trimIndent()
}

// Helper function to create and launch the share intent
private fun shareOrderDetails(context: Context, orderSummary: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, orderSummary)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share Order Summary")
    context.startActivity(shareIntent)
}

@Composable
private fun OrderDetailListItem(detail: OrderDetailWithBook) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            Text(detail.book.title, fontWeight = FontWeight.Bold,color=Color.White)
            Text("Quantity: ${detail.orderDetail.quantity}", color =Color.White)
            Text("Price per unit: £${"%.2f".format(detail.orderDetail.pricePerUnit)}", color = Color.White)
        }
    }
}