package com.example.sharedews
import java.util.Date

data class Task(
//    val taskName: String,
//    val taskNotes: String,
//    val completed: Boolean = false,
//    val listDocumentId: String,
    var taskName: String,
    var taskNotes: String,
    var completed: Boolean = false,
    var listDocumentId: String,
)

data class MyList(
    val listName: String,
    val owner: String,
    val createdOn: Date,
    val tasks: List<Task>
)

data class UserData(
    val userId: String,
    val email: String,
    val displayName: String,
    // add more as needed
)