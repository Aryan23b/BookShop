package com.example.libbook.viewmodels


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.libbook.data.local.BookShopDatabase
import com.example.libbook.data.models.Book
import com.example.libbook.data.models.CartItem
import com.example.libbook.data.remote.RetrofitInstance
import com.example.libbook.repository.BookShopRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookShopRepository
    val allBooks: StateFlow<List<Book>>

    init {
        val bookShopDao = BookShopDatabase.getDatabase(application).bookShopDao()
        repository = BookShopRepository(bookShopDao, RetrofitInstance.api)

        allBooks = repository.allBooks.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
    }

    fun addToCart(username: String, bookIsbn: String) {
        viewModelScope.launch {
            // Check if the item is already in the cart
            val cartItems = repository.getCartItemsForUser(username).first()
            val existingItem = cartItems.find { it.bookIsbn == bookIsbn }

            val newItem = if (existingItem != null) {
                // If it exists, increment quantity
                existingItem.copy(quantity = existingItem.quantity + 1)
            } else {
                // If not, create a new cart item
                CartItem(username = username, bookIsbn = bookIsbn, quantity = 1)
            }
            repository.upsertCartItem(newItem)
        }
    }
}