package com.example.libbook.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val orderId: Long = 0,
    val username: String,
    val orderDate: Long,
    val totalAmount: Double
)