package com.example.sharedews
import java.util.Date
// ListModels.kt
data class MyListItem(
    val itemName: String,
    val quantity: Int,
    val completed: Boolean)
data class MyList(
    val listItem: String,
    val createdAt: Date,
    val items: List<MyListItem>,
    val owner: String)
