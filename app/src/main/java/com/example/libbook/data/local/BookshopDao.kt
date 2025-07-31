package com.example.libbook.data.local

import androidx.room.*
import com.example.libbook.data.models.Book
import com.example.libbook.data.models.CartItem
import com.example.libbook.data.models.Order
import com.example.libbook.data.models.OrderDetail
import com.example.libbook.data.models.OrderDetailWithBook
import com.example.libbook.data.models.User
import kotlinx.coroutines.flow.Flow

@Dao
interface BookShopDao {

    @Upsert // handles both INSERT and UPDATE automatically. Perfect for the "Add Stock" feature.
    suspend fun upsertBook(book: Book)

    @Query("SELECT * FROM books ORDER BY title ASC")
    fun getAllBooks(): Flow<List<Book>> // Using Flow to get real-time UI updates

    @Query("SELECT * FROM books WHERE isbn13 = :isbn")
    suspend fun getBookByIsbn(isbn: String): Book?

    // --- User Operations ---

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUser(username: String): User?

    // --- Cart Operations ---
    @Upsert
    suspend fun upsertCartItem(item: CartItem)

    @Query("SELECT * FROM cart_items WHERE username = :username")
    fun getCartItemsForUser(username: String): Flow<List<CartItem>>

    @Query("DELETE FROM cart_items WHERE username = :username AND bookIsbn = :bookIsbn")
    suspend fun deleteCartItem(username: String, bookIsbn: String)

    @Query("DELETE FROM cart_items WHERE username = :username")
    suspend fun clearCartForUser(username: String)

    @Transaction
    @Update
    suspend fun updateBooks(books: List<Book>)

    @Insert
    suspend fun insertOrder(order: Order): Long // Returns the new orderId

    @Insert
    suspend fun insertOrderDetails(orderDetails: List<OrderDetail>)

    @Transaction
    suspend fun insertOrderAndDetails(order: Order, details: List<OrderDetail>) {
        val orderId = insertOrder(order)
        val detailsWithOrderId = details.map { it.copy(parentOrderId = orderId) }
        insertOrderDetails(detailsWithOrderId)
    }
    // ... inside BookShopRepository class ...

    @Query("SELECT * FROM orders WHERE username = :username ORDER BY orderDate DESC")
    fun getOrdersForUser(username: String): Flow<List<Order>>

    @Transaction
    @Query("""
        SELECT * FROM order_details
        INNER JOIN books ON order_details.bookIsbn = books.isbn13
        WHERE order_details.parentOrderId = :orderId
    """)
    fun getDetailedOrderInfo(orderId: Long): Flow<List<OrderDetailWithBook>>



    @Query("SELECT * FROM orders WHERE orderId = :orderId")
    suspend fun getOrderById(orderId: Long): Order?





}