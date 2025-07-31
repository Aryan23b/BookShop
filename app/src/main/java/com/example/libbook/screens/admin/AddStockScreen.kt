package com.example.libbook.screens.admin


import android.R.attr.author
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.libbook.data.models.Book
import com.example.libbook.viewmodels.StockViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStockScreen(
    navController: NavController,
    stockViewModel: StockViewModel = viewModel()
) {

    val formState by stockViewModel.formState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

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
                    title = { Text("Add/Update Stock") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {


                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = formState.isbn,
                        onValueChange = { stockViewModel.onIsbnChange(it) },
                        label = { Text("ISBN-13") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { stockViewModel.fetchBookDetails(formState.isbn) },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Fetch")
                    }
                }
                OutlinedTextField(
                    value = formState.title,
                    onValueChange = { stockViewModel.onTitleChange(it) },
                    label = { Text("Book Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = formState.author,
                    onValueChange = { stockViewModel.onAuthorChange(it) },
                    label = { Text("Author") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = formState.coverImageUrl,
                    onValueChange = { stockViewModel.onCoverImageUrlChange(it) },
                    label = { Text("Cover Image URL") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = formState.description,
                    onValueChange = { stockViewModel.onDescriptionChange(it) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                // Publication Date
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Pub Date: ${formatDate(formState.publicationDate)}",
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { showDatePicker = true }) {
                        Text("Select Date")
                    }
                }
                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        onDateSet = { newDate ->
                            stockViewModel.onPublicationDateChange(newDate)
                            showDatePicker = false
                        }
                    )
                }

                // Sliders
                SliderItem(
                    label = "Trade Price (₹)",
                    value = formState.tradePrice,
                    onValueChange = { stockViewModel.onTradePriceChange(it) },
                    valueRange = 0f..1000f
                )
                SliderItem(
                    label = "Retail Price (₹)",
                    value = formState.retailPrice,
                    onValueChange = { stockViewModel.onRetailPriceChange(it) },
                    valueRange = 0f..1000f
                )
                SliderItem(
                    label = "Quantity",
                    value = formState.quantity.toFloat(),
                    onValueChange = { stockViewModel.onQuantityChange(it.toInt()) },
                    valueRange = 0f..20f,
                    steps = 19
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        stockViewModel.saveBook()
                        navController.popBackStack() // Go back after saving
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = formState.isbn.isNotBlank() && formState.title.isNotBlank()
                ) {
                    Text("Save Book")
                }
            }
        }
    }
}

@Composable
private fun SliderItem(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int = 0
) {
    Column {
        Text(text = "$label: ${"%.2f".format(value)}")
        Slider(value = value, onValueChange = onValueChange, valueRange = valueRange, steps = steps)
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(onDismissRequest: () -> Unit, onDateSet: (Long) -> Unit) {
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    androidx.compose.material3.DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            Button(onClick = {
                datePickerState.selectedDateMillis?.let { onDateSet(it) }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) { Text("Cancel") }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}