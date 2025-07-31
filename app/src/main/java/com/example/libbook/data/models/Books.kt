package com.example.libbook.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class Book(
    @PrimaryKey
    val isbn13: String,
    val title: String,
    val author: String,
    val publicationDate: Long, // Store as a Unix timestamp for easy sorting
    val description: String,
    val coverImageUrl: String,
    val tradePrice: Float,
    val retailPrice: Float,
    val quantityInStock: Int
)