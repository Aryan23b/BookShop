package com.example.libbook.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
        @PrimaryKey
        val username: String,
        val password: String,
        val isAdmin: Boolean = false
)