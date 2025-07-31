package com.example.libbook.data.local


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.libbook.data.models.Book
import com.example.libbook.data.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.libbook.data.models.CartItem
import com.example.libbook.data.models.OrderDetail
import com.example.libbook.data.models.Order



//@Database(entities = [Book::class, User::class, CartItem::class], version = 1, exportSchema = false)
//abstract class BookShopDatabase : RoomDatabase() {

@Database(
    entities = [Book::class, User::class, CartItem::class, Order::class, OrderDetail::class],
    version = 2, // IMPORTANT: Increment the version
    exportSchema = false
     )
    abstract class BookShopDatabase : RoomDatabase() {

    abstract fun bookShopDao(): BookShopDao

    companion object {
        @Volatile
        private var INSTANCE: BookShopDatabase? = null

        fun getDatabase(context: Context): BookShopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BookShopDatabase::class.java,
                    "bookshop_database"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.bookShopDao())
                }
            }
        }

        suspend fun populateDatabase(dao: BookShopDao) {
            // Pre-populate Users
            dao.insertUser(User("customer1", "p455w0rd", isAdmin = false))
            dao.insertUser(User("customer2", "p455w0rd", isAdmin = false))
            dao.insertUser(User("admin", "p455w0rd", isAdmin = true))

            // Pre-populate Books
            dao.upsertBook(Book("9780552173323", "The Thursday Murder Club", "Richard Osman", 1599148800000L, "Four septuagenarians with a few tricks up their sleeves, a female cop with her first big case, a brutal murder.", "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1660239889l/52285160.jpg", 7.50f, 10.99f, 15))
            dao.upsertBook(Book("9780241988402", "Project Hail Mary", "Andy Weir", 1620086400000L, "Ryland Grace is the sole survivor on a desperate, last-chance mission.", "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1597814959l/54493401.jpg", 9.00f, 14.99f, 10))
            dao.upsertBook(
                Book(
                    "9780316769488",
                    "The Catcher in the Rye",
                    "J.D. Salinger",
                    20109600000L,
                    "The story of Holden Caulfield's expulsion from prep school.",
                    "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1398034300l/5107.jpg",
                    5.00f,
                    8.99f,
                    20
                )
            )
            dao.upsertBook(Book("9780743273565", "The Great Gatsby", "F. Scott Fitzgerald", -1410940800000L, "A portrait of the Jazz Age in all of its decadence and excess.", "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1490528560l/4671.jpg", 4.50f, 7.99f, 18))
            dao.upsertBook(Book("9780141187761", "1984", "George Orwell", -649872000000L, "A dystopian novel set in Airstrip One, a province of the superstate Oceania.", "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1532714506l/40961427._SX318_.jpg", 6.00f, 9.99f, 12))
            dao.upsertBook(Book("9780061120084", "To Kill a Mockingbird", "Harper Lee", -299750400000L, "A classic of modern American literature, winning the Pulitzer Prize.", "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1553383690l/2657.jpg", 7.00f, 11.99f, 15))
            dao.upsertBook(Book("9780142437247", "The Hobbit", "J.R.R. Tolkien", -1019318400000L, "A great modern classic and the prelude to The Lord of the Rings.", "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1546071216l/5907.jpg", 8.50f, 13.50f, 19))
            dao.upsertBook(Book("9780747532743", "Harry Potter and the Philosopher's Stone", "J.K. Rowling", 867244800000L, "Harry Potter's life is miserable. His parents are dead and he's stuck with his heartless relatives.", "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1474154022l/3.jpg", 9.50f, 15.99f, 8))
            dao.upsertBook(Book("9780385547345", "The Silent Patient", "Alex Michaelides", 1549324800000L, "Alicia Berenson’s life is seemingly perfect. A famous painter married to an in-demand fashion photographer.", "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1668789542l/40097951.jpg", 10.00f, 16.99f, 5))
            dao.upsertBook(Book("9780751582269", "Atomic Habits", "James Clear", 1539648000000L, "An Easy & Proven Way to Build Good Habits & Break Bad Ones.", "https://i.gr-assets.com/images/S/compressed.photo.goodreads.com/books/1655988385l/40121378.jpg", 12.00f, 19.99f, 20))
        }
    }
}