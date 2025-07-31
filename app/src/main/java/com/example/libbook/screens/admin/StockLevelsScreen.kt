package com.example.libbook.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.libbook.data.models.Book
import com.example.libbook.navigation.Screen
import com.example.libbook.viewmodels.StockViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockLevelsScreen(
    navController: NavController,
    stockViewModel: StockViewModel = viewModel()
) {
    val books by stockViewModel.allBooks.collectAsState()

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
                        titleContentColor = Color.White),
                                title = { Text("Stock Levels")
                                }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { navController.navigate(Screen.AddStock.route) }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Stock")
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(books) { book ->
                    BookStockItem(book = book)
                }
            }
        }
    }
}
@Composable
private fun BookStockItem(book: Book) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF494E8A)),

    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = book.coverImageUrl,
                contentDescription = "Book cover for ${book.title}",
                modifier = Modifier
                    .width(60.dp)
                    .height(90.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(book.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("ISBN: ${book.isbn13}", style = MaterialTheme.typography.bodySmall)
                Text("In Stock: ${book.quantityInStock}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}