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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


data class AddStockFormState(
val title: String = "",
val author: String = "",
val isbn: String = "",
val description: String = "",
val coverImageUrl: String = "",
val tradePrice: Float = 10f,
val retailPrice: Float = 15f,
val quantity: Int = 5,
val publicationDate: Long = System.currentTimeMillis()
)

class StockViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookShopRepository

    // Holds all books for the StockLevelsScreen
    val allBooks: StateFlow<List<Book>>

    // Holds the state for the AddStockScreen form
    private val _formState = MutableStateFlow(AddStockFormState())
    val formState: StateFlow<AddStockFormState> = _formState.asStateFlow()


    init {
        val bookShopDao = BookShopDatabase.getDatabase(application).bookShopDao()
        repository = BookShopRepository(bookShopDao, RetrofitInstance.api)

        // Convert the repository's Flow into a hot StateFlow that the UI can collect.
        allBooks = repository.allBooks.stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
    }

    // Function to fetch details from the API
    fun fetchBookDetails(isbn: String) {
        viewModelScope.launch {
            val details = repository.fetchBookDetailsFromApi(isbn)
            if (details != null) {
                _formState.value = _formState.value.copy(
                    title = details.title ?: "",
                    author = details.authors?.joinToString(", ") ?: "",
                    description = details.description ?: "",
                    coverImageUrl = details.imageLinks?.thumbnail?.replace("http://", "https://") ?: ""
                )
            }
        }
    }

    // Function to save the book using the form state
    fun saveBook() {
        viewModelScope.launch {
            val currentState = _formState.value
            val book = Book( // Creates the Book object with the fields you have
                isbn13 = currentState.isbn,
                title = currentState.title,
                author = currentState.author,
                description = currentState.description,
                publicationDate = currentState.publicationDate,
                tradePrice = currentState.tradePrice,
                retailPrice = currentState.retailPrice,
                quantityInStock = currentState.quantity,
                coverImageUrl = currentState.coverImageUrl
            )
            repository.upsertBook(book)
            _formState.value = AddStockFormState() // Reset form
        }
    }

    // --- Functions to update each form field ---
    fun onIsbnChange(isbn: String) { _formState.value = _formState.value.copy(isbn = isbn) }
    fun onTitleChange(title: String) { _formState.value = _formState.value.copy(title = title) }
    fun onAuthorChange(author: String) { _formState.value = _formState.value.copy(author = author) }
    fun onDescriptionChange(desc: String) { _formState.value = _formState.value.copy(description = desc) }
    fun onCoverImageUrlChange(url: String) { _formState.value = _formState.value.copy(coverImageUrl = url) } // Changed
    fun onTradePriceChange(price: Float) { _formState.value = _formState.value.copy(tradePrice = price) }
    fun onRetailPriceChange(price: Float) { _formState.value = _formState.value.copy(retailPrice = price) }
    fun onQuantityChange(qty: Int) { _formState.value = _formState.value.copy(quantity = qty) }
    fun onPublicationDateChange(date: Long) { _formState.value = _formState.value.copy(publicationDate = date) }
}


