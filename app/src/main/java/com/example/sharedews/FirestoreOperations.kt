package com.example.sharedews

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await




object FirestoreOperations {
    private val collection: CollectionReference by lazy {
        FirebaseFirestore.getInstance().collection("lists")
    }


    // Suspend function to update the list name in Firestore
    suspend fun updateListNameInFirestore(listName: String, newName: String) {
        try {
            val querySnapshot = collection.whereEqualTo("listName", listName).get().await()

            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents.first()
                documentSnapshot.reference.update("listName", newName).await()
            } else {
                // Handle the case where the list with the given name doesn't exist
                Log.e("Firestore", "List with name $listName not found.")
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error updating list name: ${e.message}")
        }
    }

    // Suspend function to fetch list items from Firestore
    suspend fun fetchTasksFromFirestore(listName: String): List<Task> {
        return try {
            val documentSnapshot = collection.document(listName).get().await()
            val tasksArray = documentSnapshot.get("tasks") as? List<String>
            val tasksList = tasksArray?.map { Task(it, "") } ?: emptyList()
            tasksList
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Firestore", "Error fetching list tasks: ${e.message}")
            emptyList()
        }
    }
}