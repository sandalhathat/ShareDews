package com.example.sharedews
import java.util.Date

data class ListItem(
    val itemName: String,
    val taskNotes: String
)

data class MyList(
    val listName: String,
    val owner: String,
    val createdOn: Date,
    val items: List<ListItem>
)
