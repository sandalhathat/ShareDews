package com.example.sharedews

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
fun ListDetailScreen(navController: NavController, listName: String) {
    var newListName by remember { mutableStateOf(listName) }
    var isEditing by remember { mutableStateOf(false) }
    // Firestore
    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("lists")

    // state to manage bottom sheet visibility
    var isCreateTaskSheetVisible by remember { mutableStateOf(false) }

    // func to open bottom sheet
    fun openCreateTaskSheet() {
        isCreateTaskSheetVisible = true
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
    suspend fun fetchListItemsFromFirestore(): List<ListItem> {
        return try {
            val documentSnapshot = collection.document(listName).get().await()
            val itemsArray = documentSnapshot.get("items") as? List<String>
            val itemsList = itemsArray?.map { ListItem(it, "") } ?: emptyList()
            itemsList
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("Firestore", "Error fetching list items: ${e.message}")
            emptyList()
        }
    }

    // Create a mutable list of list items
    var listItems by remember { mutableStateOf(emptyList<ListItem>()) }

    LaunchedEffect(true) {
        listItems = fetchListItemsFromFirestore()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = Color.DarkGray)
    ) {

        // back button
        IconButton(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier
                .padding(4.dp)
                .background(color = Color.Magenta)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isEditing) {
                BasicTextField(
                    value = newListName,
                    onValueChange = {
                        newListName = it
                    },
                    textStyle = LocalTextStyle.current,
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp)
                )
            } else {
                Text(
                    text = newListName,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(4.dp)
                )
            }
            IconButton(
                onClick = {
                    if (isEditing) {
                        // Save changes to Firestore
                        val finalNewListName = newListName // create a final var?
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                updateListNameInFirestore(listName, newListName)
                                withContext(Dispatchers.Main) {
                                    isEditing = false
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        isEditing = true
                    }
                },
                modifier = Modifier
                    .padding(4.dp)
                    .background(color = Color.DarkGray)
            ) {
                if (isEditing) {
                    Icon(
                        imageVector = Icons.Default.Done,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }


        // Render the list of items
        ListItems(listItems)


        // Button to add a new item
        Button(
            onClick = {
                // open bottom sheet to create new task
                openCreateTaskSheet()
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Create Task")
        }

        // button sheet to create a new task
        if (isCreateTaskSheetVisible) {
            CreateTaskScreen(
                navController = navController,
                onTaskCreated = { taskName, taskNotes ->

                    // add new task to list
                    listItems = listItems.toMutableList() + ListItem(taskName, taskNotes)

                    // close bottom sheet
                    isCreateTaskSheetVisible = false
                }
            )
        }
    }
}

@Composable
fun ListItems(listItems: List<ListItem>) {
    if (listItems.isEmpty()) {
        Text(text = "No items available.")
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {

            items(listItems) { item ->
                // Destructure the item to access its properties
                val itemName = item.itemName
                val itemNotes = item.taskNotes
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Handle item click here
                        }
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
//                                text = text, // Use the extracted 'text' property
                                text = itemName,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = itemNotes,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}
