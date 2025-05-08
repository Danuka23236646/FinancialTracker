package com.example.newapp2.data

import java.util.Date

data class Transaction(
    val id: Long = System.currentTimeMillis(),
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val description: String,
    val date: Date = Date()
)

enum class TransactionType {
    INCOME,
    EXPENSE
} 