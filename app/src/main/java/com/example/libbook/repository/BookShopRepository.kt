package com.example.libbook.repository


import com.example.libbook.data.local.BookShopDao
import com.example.libbook.data.models.Book
import com.example.libbook.data.models.CartItem
import com.example.libbook.data.models.Order
import com.example.libbook.data.models.OrderDetail
import com.example.libbook.data.models.OrderDetailWithBook
import com.example.libbook.data.models.User
import com.example.libbook.data.remote.GoogleBooksApiService
import com.example.libbook.data.remote.VolumeInfo
import kotlinx.coroutines.flow.Flow

class BookShopRepository(private val bookShopDao: BookShopDao,
                         private val apiService: GoogleBooksApiService
) {
    //  Book Functions

    // Exposes a Flow of all books from the database for real-time updates.

    val allBooks: Flow<List<Book>> = bookShopDao.getAllBooks()

    suspend fun getBookByIsbn(isbn: String): Book? {
        return bookShopDao.getBookByIsbn(isbn)
    }

    suspend fun upsertBook(book: Book) {
        bookShopDao.upsertBook(book)
    }

    // Add this new function
    suspend fun updateStockForBooks(books: List<Book>) {
        bookShopDao.updateBooks(books)
    }

    // --- User Functions ---

    suspend fun getUser(username: String): User? {
        return bookShopDao.getUser(username)
    }

    // --- Cart Functions ---

    fun getCartItemsForUser(username: String): Flow<List<CartItem>> {
        return bookShopDao.getCartItemsForUser(username)
    }

    suspend fun upsertCartItem(cartItem: CartItem) {
        bookShopDao.upsertCartItem(cartItem)
    }

    suspend fun deleteCartItem(username: String, bookIsbn: String) {
        bookShopDao.deleteCartItem(username, bookIsbn)
    }

    suspend fun clearCartForUser(username: String) {
        bookShopDao.clearCartForUser(username)
    }

    //to save order
    suspend fun saveOrder(order: Order, details: List<OrderDetail>) {
        bookShopDao.insertOrderAndDetails(order, details)
    }

    suspend fun getOrderById(orderId: Long): Order?{
        return bookShopDao.getOrderById(orderId)
    }

    // Add these new functions
    fun getOrdersForUser(username: String): Flow<List<Order>> {
        return bookShopDao.getOrdersForUser(username)
    }

    fun getDetailedOrderInfo(orderId: Long): Flow<List<OrderDetailWithBook>> {
        return bookShopDao.getDetailedOrderInfo(orderId)
    }

    suspend fun fetchBookDetailsFromApi(isbn: String): VolumeInfo? {
        return try {
            val response = apiService.searchByIsbn("isbn:$isbn")
            if (response.isSuccessful) {
                // Return the volumeInfo of the first found item
                response.body()?.items?.firstOrNull()?.volumeInfo
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
