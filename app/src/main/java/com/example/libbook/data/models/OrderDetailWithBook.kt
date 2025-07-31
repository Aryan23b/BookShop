package com.example.libbook.data.models


import androidx.room.Embedded

data class OrderDetailWithBook(
    @Embedded
    val orderDetail: OrderDetail,
    @Embedded
    val book: Book
)