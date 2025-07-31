package com.example.libbook.viewmodels



import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.libbook.data.local.BookShopDatabase
import com.example.libbook.data.models.Book
import com.example.libbook.data.models.Order
import com.example.libbook.data.models.OrderDetail
import com.example.libbook.data.remote.RetrofitInstance
import com.example.libbook.repository.BookShopRepository
import com.example.libbook.utlis.LocationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Date


// Represents the stock status of an item in the final order
sealed class StockStatus {
    data object InStock : StockStatus()
    data class InsufficientStock(val available: Int) : StockStatus()
}

// Represents a single item in the final checkout summary
data class FinalOrderItem(
    val book: Book,
    val quantityOrdered: Int,
    val status: StockStatus
)

// Represents the entire state of the checkout screen
data class CheckoutUiState(
    val items: List<FinalOrderItem> = emptyList(),
    val subtotal: Double = 0.0,
    val postage: Double = 0.0,
    val total: Double = 0.0,
    val orderPlacementStatus: OrderPlacementStatus = OrderPlacementStatus.Idle,

    // Add address fields
    val addressLine: String = "",
    val city: String = "",
    val postalCode: String = "",
    val country: String = ""
)

sealed class OrderPlacementStatus {
    data object Idle : OrderPlacementStatus()
    data object Success : OrderPlacementStatus()
}

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookShopRepository
    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState

    init {
        val bookShopDao = BookShopDatabase.getDatabase(application).bookShopDao()
        repository = BookShopRepository(bookShopDao, RetrofitInstance.api)
    }

    fun prepareOrder(username: String) {
        viewModelScope.launch {
            val cartItems = repository.getCartItemsForUser(username).first()
            val allBooks = repository.allBooks.first()
            val finalOrderItems = mutableListOf<FinalOrderItem>()

            for (cartItem in cartItems) {
                val book = allBooks.find { it.isbn13 == cartItem.bookIsbn }
                if (book != null) {
                    val status = if (book.quantityInStock >= cartItem.quantity) {
                        StockStatus.InStock
                    } else {
                        StockStatus.InsufficientStock(book.quantityInStock)
                    }
                    finalOrderItems.add(
                        FinalOrderItem(
                            book = book,
                            quantityOrdered = cartItem.quantity,
                            status = status
                        )
                    )
                }
            }

            val inStockItems = finalOrderItems.filter { it.status is StockStatus.InStock }
            val subtotal = inStockItems.sumOf { it.book.retailPrice.toDouble() * it.quantityOrdered }
            val postage = if (inStockItems.isNotEmpty()) 3.0 + (inStockItems.size - 1) * 1.0 else 0.0
            val total = subtotal + postage

            _uiState.value = CheckoutUiState(
                items = finalOrderItems,
                subtotal = subtotal,
                postage = postage,
                total = total
            )
        }
    }
    fun onAddressLineChange(newAddress: String) {
        _uiState.value = _uiState.value.copy(addressLine = newAddress)
    }

    fun onCityChange(newCity: String) {
        _uiState.value = _uiState.value.copy(city = newCity)
    }

    fun onPostalCodeChange(newPostalCode: String) {
        _uiState.value = _uiState.value.copy(postalCode = newPostalCode)
    }

    fun onCountryChange(newCountry: String) {
        _uiState.value = _uiState.value.copy(country = newCountry)
    }

    fun fetchCurrentLocation(context: Context) {
        LocationUtils.getAddressFromLocation(context) { address ->
            if (address != null) {
                _uiState.value = _uiState.value.copy(
                    addressLine = address.getAddressLine(0) ?: "",
                    city = address.locality ?: "",
                    postalCode = address.postalCode ?: "",
                    country = address.countryName ?: ""
                )
            }
        }
    }

    fun placeOrder(username: String) {
        viewModelScope.launch {
            val currentState = _uiState.value
            val itemsToPurchase = currentState.items.filter { it.status is StockStatus.InStock }

            if (itemsToPurchase.isNotEmpty()) {
                // 1. Create Order and OrderDetail objects
                val newOrder = Order(
                    username = username,
                    orderDate = Date().time,
                    totalAmount = currentState.total
                )
                val newOrderDetails = itemsToPurchase.map {
                    OrderDetail(
                        parentOrderId = 0, // This will be replaced by the DAO
                        bookIsbn = it.book.isbn13,
                        quantity = it.quantityOrdered,
                        pricePerUnit = it.book.retailPrice.toDouble()
                    )
                }

                // 2. Save the order to the database
                repository.saveOrder(newOrder, newOrderDetails)

                // 3. Update stock levels
                val updatedBooks = itemsToPurchase.map {
                    it.book.copy(quantityInStock = it.book.quantityInStock - it.quantityOrdered)
                }
                repository.updateStockForBooks(updatedBooks)

                // 4. Clear the cart
                repository.clearCartForUser(username)

                // 5. Update UI state
                _uiState.value =
                    currentState.copy(orderPlacementStatus = OrderPlacementStatus.Success)
            }
        }
    }
}