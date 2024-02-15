package com.example.sharedews

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


object FirestoreOps {
    private val collection: CollectionReference by lazy {
        FirebaseFirestore.getInstance().collection("lists")
    }

    // Suspend function to fetch list document ID from Firestore
    suspend fun fetchListDocumentIdFromFirestore(listName: String): String? {
        return try {
            val querySnapshot = collection.whereEqualTo("listName", listName).get().await()

            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents.first()
                documentSnapshot.id
            } else {
                // Handle the case where the list with the given name doesn't exist
                Log.e("Firestore", "List with name $listName not found.")
                null
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching list document ID: ${e.message}")
            null
        }
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
    // Suspend function to fetch list items from Firestore
    suspend fun fetchTasksFromFirestore(listDocumentId: String): List<Task> {
        return try {
            val documentSnapshot = collection.document(listDocumentId).get().await()
            Log.d("Firestore", "DocumentSnapshot: $documentSnapshot") // Log the entire document snapshot
            val tasksArray = documentSnapshot.get("tasks")
            Log.d("Firestore", "TasksArray type: ${tasksArray?.javaClass}")
            Log.d("Firestore", "TasksArray value: $tasksArray")

            if (tasksArray is List<*>) {
                val tasksList = tasksArray.mapNotNull { taskName ->
                    if (taskName is String) {
                        Task(taskName, "")
                    } else {
                        null
                    }
                }

                Log.d("Firestore", "Fetched tasks: $tasksList")
                tasksList
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Firestore", "Error fetching list tasks: ${e.message}")
            emptyList()
        }
    }


    // suspend function to delete a list from firestore
    suspend fun deleteListFromFirestore(listName: String) {
        try {
            val querySnapshot = collection.whereEqualTo("listName", listName).get().await()
            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents.first()
                documentSnapshot.reference.delete().await()
            } else {
                // Handle the case where the list with the given name doesn't exist
                Log.e("Firestore", "List with name $listName not found.")
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error deleting list: ${e.message}")
        }
    }

    suspend fun saveTaskToFirestore(listDocumentId: String, taskName: String, taskNotes: String) {
        try {
            val collection = FirebaseFirestore.getInstance().collection("lists")
            val documentReference = collection.document(listDocumentId)
            documentReference.update("tasks", FieldValue.arrayUnion(taskName)).await()
            val task = hashMapOf(
                "taskName" to taskName,
                "taskNotes" to taskNotes
            )

            // Create a subcollection for tasks within the list document
            documentReference.collection("tasks").add(task).await()
        } catch (e: Exception) {
            Log.e("FirestoreOps", "Error saving task to Firestore: ${e.message}")
        }
    }

    suspend fun deleteTask(listDocumentId: String, taskName: String) {
        try {
            val documentReference = collection.document(listDocumentId)
            documentReference.update("tasks", FieldValue.arrayRemove(taskName)).await()
        } catch (e: Exception) {
            Log.e("FirestoreOps", "Error deleting task from Firestore: ${e.message}")
        }
    }


    suspend fun completeTask(listDocumentId: String, taskName: String) {
        try {
            val documentReference = collection.document(listDocumentId)
            documentReference.collection("tasks").document(taskName)
                .update("completed", true).await()
        } catch (e: Exception) {
            Log.e("FirestoreOps", "Error completing task in Firestore: ${e.message}")
        }
    }


    suspend fun editTask(listDocumentId: String, taskName: String, newTaskName: String, newTaskNotes: String) {
        try {
            val documentReference = collection.document(listDocumentId)
            documentReference.collection("tasks").document(taskName)
                .update(mapOf("taskName" to newTaskName, "taskNotes" to newTaskNotes)).await()
        } catch (e: Exception) {
            Log.e("FirestoreOps","Error editing task in Firestore: ${e.message}")
        }

    }



}