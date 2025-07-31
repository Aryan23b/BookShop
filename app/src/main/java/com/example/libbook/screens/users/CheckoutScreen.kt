package com.example.libbook.screens.users


import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.libbook.navigation.Screen
import com.example.libbook.viewmodels.CheckoutViewModel
import com.example.libbook.viewmodels.FinalOrderItem
import com.example.libbook.viewmodels.OrderPlacementStatus
import com.example.libbook.viewmodels.StockStatus
import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavController,
    username: String,
    checkoutViewModel: CheckoutViewModel = viewModel()
) {
    val uiState by checkoutViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                checkoutViewModel.fetchCurrentLocation(context)
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(username) {
        checkoutViewModel.prepareOrder(username)
    }

    LaunchedEffect(uiState.orderPlacementStatus) {
        if (uiState.orderPlacementStatus is OrderPlacementStatus.Success) {
            Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_LONG).show()
            navController.navigate(Screen.CustomerHome.createRoute(username)) {
                popUpTo(Screen.CustomerHome.createRoute(username)) { inclusive = true }
            }
        }
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
                    title = { Text("Complete Order") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",
                                tint = Color.White)
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                //order summary
                Text("Order Summary", style = MaterialTheme.typography.titleLarge, color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(uiState.items) { item ->
                        FinalOrderListItem(item = item)
                        Divider()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                // --- Shipping Address ---
                Text("Shipping Address", style = MaterialTheme.typography.titleLarge,color=Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
                    colors = ButtonDefaults.buttonColors
                        (containerColor = Color(0xFF9BA9FF))) {
                    Text("Use Current Location")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.addressLine,
                    onValueChange = { checkoutViewModel.onAddressLineChange(it) },
                    label = { Text("Address Line") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White)
                )
                OutlinedTextField(
                    value = uiState.city,
                    onValueChange = { checkoutViewModel.onCityChange(it) },
                    label = { Text("City") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = Color.White)
                )
                OutlinedTextField(
                    value = uiState.postalCode,
                    onValueChange = { checkoutViewModel.onPostalCodeChange(it) },
                    label = { Text("Postal Code") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color=Color.White)
                )
                OutlinedTextField(
                    value = uiState.country,
                    onValueChange = { checkoutViewModel.onCountryChange(it) },
                    label = { Text("Country") },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color=Color.White)
                )


                CostSummary(
                    subtotal = uiState.subtotal,
                    postage = uiState.postage,
                    total = uiState.total
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { checkoutViewModel.placeOrder(username) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.subtotal > 0,
                    colors = ButtonDefaults.buttonColors
                        (containerColor = Color(0xFF9BA9FF))

                ) {
                    Text("Pay Now - ₹${"%.2f".format(uiState.total)}")
                }
            }
        }
    }
}

@Composable
private fun FinalOrderListItem(item: FinalOrderItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.book.title, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Qty: ${item.quantityOrdered}", color = Color.White)
            when (val status = item.status) {
                is StockStatus.InStock -> {
                    Text("Price: ₹${"%.2f".format(item.book.retailPrice)}", color = Color.White)
                }
                is StockStatus.InsufficientStock -> {
                    Text(
                        text = "Price: ₹${"%.2f".format(item.book.retailPrice)}",
                        textDecoration = TextDecoration.LineThrough,
                        color = Color.White
                    )
                    Text("Only ${status.available} in stock. Item removed from total.", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun CostSummary(subtotal: Double, postage: Double, total: Double) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
        Text("Subtotal: ₹${"%.2f".format(subtotal)}", style = MaterialTheme.typography.bodyLarge,color=Color.White)
        Text("Postage: ₹${"%.2f".format(postage)}", style = MaterialTheme.typography.bodyLarge,color=Color.White)
        Text("Total: ₹${"%.2f".format(total)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold,color=Color.White)
    }
}