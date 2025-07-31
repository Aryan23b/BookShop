package com.example.libbook.data.models

import androidx.room.Entity

@Entity(tableName = "cart_items", primaryKeys = ["username", "bookIsbn"])
data class CartItem(
    val username: String,
    val bookIsbn: String,
    val quantity: Int
)