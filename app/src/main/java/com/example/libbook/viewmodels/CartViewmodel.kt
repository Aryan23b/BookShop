package com.example.libbook.viewmodels


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.libbook.data.local.BookShopDatabase
import com.example.libbook.data.models.Book
import com.example.libbook.data.remote.RetrofitInstance
import com.example.libbook.repository.BookShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

// A new data class to hold the combined cart item details for the UI
data class CartItemDetails(
    val book: Book,
    val quantityInCart: Int
)

// Represents the entire state of the shopping cart screen
data class CartUiState(
    val items: List<CartItemDetails> = emptyList(),
    val totalPrice: Double = 0.0
)

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookShopRepository
    private val _cartState = MutableStateFlow(CartUiState())
    val cartState: StateFlow<CartUiState> = _cartState

    init {
        val bookShopDao = BookShopDatabase.getDatabase(application).bookShopDao()
        repository = BookShopRepository(bookShopDao, RetrofitInstance.api)
    }

    fun loadCart(username: String) {
        viewModelScope.launch {
            // Combine the flow of cart items with the flow of all books
            repository.getCartItemsForUser(username)
                .combine(repository.allBooks) { cartItems, books ->
                    val detailedItems = cartItems.mapNotNull { cartItem ->
                        books.find { it.isbn13 == cartItem.bookIsbn }
                            ?.let { book ->
                                CartItemDetails(book = book, quantityInCart = cartItem.quantity)
                            }
                    }
                    val total = detailedItems.sumOf { it.book.retailPrice.toDouble() * it.quantityInCart }
                    CartUiState(items = detailedItems, totalPrice = total)
                }.collect { state ->
                    _cartState.value = state
                }
        }
    }

    fun deleteFromCart(username: String, bookIsbn: String) {
        viewModelScope.launch {
            repository.deleteCartItem(username, bookIsbn)
        }
    }

    // Add this new function
    fun clearCart(username: String) {
        viewModelScope.launch {
            repository.clearCartForUser(username)
        }
    }
}
