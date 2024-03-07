package com.example.sharedews

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.Date


object FirestoreOps {
    private val collection: CollectionReference by lazy {
        FirebaseFirestore.getInstance().collection("lists")
    }

    suspend fun saveTaskFS(listDocumentId: String, taskName: String, taskNotes: String) {
        try {
            val documentReference = collection.document(listDocumentId).collection("tasks")
                .add(mapOf("taskName" to taskName, "taskNotes" to taskNotes, "completed" to false))
                .await()
            val taskId = documentReference.id
        } catch (e: Exception) {
            Log.e("FirestoreOps", "Error saving task to Firestore: ${e.message}")
        }
    }

    // Updated function to fetch tasks
    suspend fun fetchTasksFS(listDocumentId: String): List<Task> {
        return try {
            val tasksCollection = collection.document(listDocumentId).collection("tasks")
            val querySnapshot = tasksCollection.get().await()
//            val tasksList = querySnapshot.documents.mapNotNull { document ->
            var tasksList = querySnapshot.documents.mapNotNull { document ->
                val taskName = document["taskName"] as? String
                val taskNotes = document["taskNotes"] as? String
                val completed = document["completed"] as? Boolean
//                val documentId = document["listDocumentId"] as? String

                if (taskName != null && taskNotes != null && completed != null) {
                    Task(taskName, taskNotes, completed, listDocumentId)
                } else {
                    null
                }
            }

            Log.d("Firestore", "Fetched tasks: $tasksList")
            tasksList

        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching list tasks: ${e.message}")
            emptyList()
        }
    }

    // Updated function to delete task
    suspend fun deleteTaskFS(listDocumentId: String, taskId: String) {
        try {
            val documentReference =
                collection.document(listDocumentId).collection("tasks").document(taskId)
            documentReference.delete().await()
        } catch (e: Exception) {
            Log.e("FirestoreOps", "Error deleting task from Firestore: ${e.message}")
        }
    }

    // Updated function to complete task
    suspend fun completeTaskFS(listDocumentId: String, taskId: String) {
        try {
            val documentReference =
                collection.document(listDocumentId).collection("tasks").document(taskId)

            documentReference.update("completed", true).await()
        } catch (e: Exception) {
            Log.e("FirestoreOps", "Error completing task in Firestore: ${e.message}")
        }
    }

    // Updated function to edit task
    suspend fun editTaskFS(
        listDocumentId: String, taskId: String, newTaskName: String, newTaskNotes: String
    ) {
        try {
            Log.d(
                "FirestoreOps",
                "Editing task - listDocumentId: $listDocumentId, taskId: $taskId, newTaskName: $newTaskName, newTaskNotes: $newTaskNotes"
            )
            val documentReference =
                collection.document(listDocumentId).collection("tasks").document(taskId)
            documentReference.update(
                mapOf("taskName" to newTaskName, "taskNotes" to newTaskNotes)
            ).await()
            Log.d("FirestoreOps", "Task edited successfully")
        } catch (e: Exception) {
            Log.e("FirestoreOps", "Error editing task in Firestore: ${e.message}")
        }
    }


    // suspend func to create new list in firestore
    suspend fun saveListFS(
        listName: String, owner: String, callback: (String) -> Unit
    ) {
        val firestore = Firebase.firestore
        val listsCollection = firestore.collection("lists")
        val listDocumentId = listsCollection.document().id
        val newList = MyList(
            listName = listName, createdOn = Date(), tasks = emptyList(), owner = owner
        )

        // set data using generated doc id?
        listsCollection.document(listDocumentId).set(newList).addOnSuccessListener {
            Log.d(
                "Firestore",
                "List $listName created successfully by $owner with document ID: $listDocumentId"
            )
            // call the callback with list name and doc id
            callback("$listName $listDocumentId")
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Error creating list $listName", e)
            // handle error, show msg, etc
        }
    }


    // Suspend function to fetch list document ID from Firestore
    suspend fun fetchListDocumentIdFS(listName: String): String? {
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
    suspend fun updateListNameFS(listName: String, newName: String) {
        try {
            val querySnapshot = collection.whereEqualTo("listName", listName).get().await()

            if (!querySnapshot.isEmpty) {
                val documentSnapshot = querySnapshot.documents.first()
                documentSnapshot.reference.update("listName", newName).await()
                Log.e(
                    "Firestore",
                    "Testing updateListNameInFirestore, so here's: $listName, and here's $newName."
                )
            } else {
                // Handle the case where the list with the given name doesn't exist
                Log.e("Firestore", "List with name $listName not found.")
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error updating list name: ${e.message}")
        }
    }


    // Suspend function to delete a list from Firestore
    suspend fun deleteListFS(listName: String) {
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


    fun addUserDataToDbFS(user: FirebaseUser, userData: UserData) { //should this be suspend?
        val db = Firebase.firestore

        // check if user email is verified
        if (user.isEmailVerified) {
            // add data to database
            db.collection("users").document(user.uid).set(userData).addOnSuccessListener {
                // data added successfully
            }.addOnFailureListener { e ->
                // handle the error
            }
        } else {
            // handle case where user email is not verified
        }
    }


}