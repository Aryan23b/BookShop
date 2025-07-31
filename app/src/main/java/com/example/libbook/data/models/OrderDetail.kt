package com.example.libbook.data.models


import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "order_details",
    foreignKeys = [ForeignKey(
        entity = Order::class,
        parentColumns = ["orderId"],
        childColumns = ["parentOrderId"],
        onDelete = ForeignKey.CASCADE // If an order is deleted, its details are also deleted
    )]
)
data class OrderDetail(
    @PrimaryKey(autoGenerate = true)
    val orderDetailId: Long = 0,
    val parentOrderId: Long,
    val bookIsbn: String,
    val quantity: Int,
    val pricePerUnit: Double
)