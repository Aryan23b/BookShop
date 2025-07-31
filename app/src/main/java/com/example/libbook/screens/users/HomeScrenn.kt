package com.example.libbook.screens.users


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.libbook.data.models.Book
import com.example.libbook.navigation.Screen
import com.example.libbook.viewmodels.CartViewModel
import com.example.libbook.viewmodels.HomeViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    username: String,
    homeViewModel: HomeViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel(),
) {
    val books by homeViewModel.allBooks.collectAsState()
    val cartState by cartViewModel.cartState.collectAsState()


    // Load the cart for the logged-in user
    LaunchedEffect(username) {
        cartViewModel.loadCart(username)
    }

    // Handle the result from the barcode scanner screen
    val scannerResult = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<String>("scanned_isbn")?.observeAsState()

    LaunchedEffect(scannerResult) {
        scannerResult?.value?.let { isbn ->
            homeViewModel.addToCart(username, isbn)
            // Clear the result so it's not processed again on recomposition
            navController.currentBackStackEntry?.savedStateHandle?.remove<String>("scanned_isbn")
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF494E8A), // Using a theme color
                    titleContentColor = Color.White                  // Color for the title
                ),
                title = { Text("Book Shop") },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.BarcodeScanner.route) }) {
                        Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = "Scan Barcode",
                            tint = Color.White)
                    }
                    // Order History Button
                    IconButton(onClick = { navController.navigate(Screen.OrderHistory.createRoute(username)) }) {
                        Icon(imageVector = Icons.Default.History, contentDescription = "Order History",
                            tint=Color.White)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { navController.navigate(Screen.ShoppingCart.createRoute(username)) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Shopping Cart",
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${cartState.items.sumOf { it.quantityInCart }} items | ₹${"%.2f".format(cartState.totalPrice)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(books.filter { it.quantityInStock > 0 }) { book ->
                BookGridItem(
                    book = book,
                    onAddToCart = { homeViewModel.addToCart(username, book.isbn13) }
                )
            }
        }
    }
}

@Composable
private fun BookGridItem(
    book: Book,
    onAddToCart: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF494E8A)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = book.coverImageUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onAddToCart, modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors
                    (containerColor = Color(0xFF9BA9FF))) {
                Text("Add to Cart")
            }
        }
    }
}