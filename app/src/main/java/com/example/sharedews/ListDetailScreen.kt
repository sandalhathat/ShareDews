package com.example.sharedews

import android.util.Log
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
fun ListDetailScreen(
    navController: NavController,
    listName: String
) {
    var newListName by remember { mutableStateOf(listName) }
    var isEditing by remember { mutableStateOf(false) }
    // Firestore
    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("lists")

    // Suspend function to update the list name in Firestore
    suspend fun updateListNameInFirestore(newName: String) {
        try {
            collection.document(listName)
                .update("listName", newName)
                .await()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                e.printStackTrace()
            }
        }
    }

    // Suspend function to fetch list items from Firestore
    suspend fun fetchListItemsFromFirestore(): List<ListItem> {
        return try {
            val documentSnapshot = collection.document(listName).get().await()
            val items = documentSnapshot.getString("items") ?: ""
            // Split the items string into a list based on some delimiter...? eg, a comma?
            val itemsList = items.split(",")
                .filter { it.isNotBlank() }
                .map { ListItem(it) }
            itemsList
        }catch (e: Exception) {
            e.printStackTrace()
            Log.e("Firestore", "Error fetching list items: ${e.message}")
            emptyList()
        }
    }

    // Create a mutable list of list items
//    var listItems by remember { mutableStateOf(generateListItems()) }

    var listItems by remember { mutableStateOf(emptyList<ListItem>()) }

    LaunchedEffect(true) {
        listItems = fetchListItemsFromFirestore()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                updateListNameInFirestore(newListName)
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
                modifier = Modifier.padding(4.dp)
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
                // Add a new item to the list
                listItems = listItems.toMutableList() + ListItem("New Item")
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Add Item")
        }
    }
}

@Composable
fun ListItems(listItems: List<ListItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(listItems) { item ->
            // Destructure the item to access its properties
            val text = item.text
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
                    CustomIcon(
                        icon = Icons.Default.Star,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = text, // Use the extracted 'text' property
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Additional Info",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

data class ListItem(val text: String)

@Composable
fun CustomIcon(
    icon: ImageVector,
    tint: androidx.compose.ui.graphics.Color
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = tint
    )
}