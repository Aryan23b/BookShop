package com.example.libbook.screens.users



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.libbook.navigation.Screen
import com.example.libbook.viewmodels.CartItemDetails
import com.example.libbook.viewmodels.CartViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingCartScreen(
    navController: NavController,
    username: String,
    cartViewModel: CartViewModel = viewModel()
) {
    val cartState by cartViewModel.cartState.collectAsState()

    LaunchedEffect(username) {
        cartViewModel.loadCart(username)
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
                    title = { Text("Your Shopping Cart") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back",tint=Color.White)
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
            ) {
                if (cartState.items.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Your cart is empty.",color=Color.White)
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(cartState.items) { item ->
                            CartListItem(
                                item = item,
                                onDelete = {
                                    cartViewModel.deleteFromCart(
                                        username,
                                        item.book.isbn13
                                    )
                                }
                            )
                            Divider()
                            Spacer(modifier= Modifier.padding(5.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Total: ₹${"%.2f".format(cartState.totalPrice)}",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.align(Alignment.End)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(onClick = {
                            cartViewModel.clearCart(username)
                            navController.popBackStack()
                        }) {
                            Text("Cancel Order", color = Color.White)
                        }
                        Button(onClick = {
                            // Navigate to the Checkout screen, passing the username
                            navController.navigate(Screen.Checkout.createRoute(username))
                        },
                            colors = ButtonDefaults.buttonColors
                                (containerColor = Color(0xFF494E8A))) {
                            Text("Checkout")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CartListItem(
    item: CartItemDetails,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.book.coverImageUrl,
            contentDescription = item.book.title,
            modifier = Modifier.size(width = 80.dp, height = 120.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(item.book.title, fontWeight = FontWeight.Bold,color=Color.White)
            Text("Price: ₹${"%.2f".format(item.book.retailPrice)}", color = Color.White)
            Text("Quantity: ${item.quantityInCart}",color=Color.White)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Remove Item",tint=Color.White)
        }
    }
}