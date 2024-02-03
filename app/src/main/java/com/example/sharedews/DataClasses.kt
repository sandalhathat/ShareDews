package com.example.sharedews
import java.util.Date

data class Task(
    val taskName: String,
    val taskNotes: String
)

data class MyList(
    val listName: String,
    val owner: String,
    val createdOn: Date,
    val items: List<Task>
)

data class UserData(
    val userId: String,
    val email: String,
    val displayName: String,
    // add more as needed
)