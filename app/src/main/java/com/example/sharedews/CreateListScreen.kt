package com.example.sharedews

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.Date

@Composable
fun CreateListScreen(navController: NavController, onListCreated: (String) -> Unit) {
    var newListName by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = Color.White)
    ) {


        // Back button
        // Back button
        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            Text(text = stringResource(id = R.string.back))
        }

        // List Name input
        BasicTextField(
            value = newListName,
            onValueChange = { newInput -> newListName = newInput },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .background(color = Color.LightGray)
        )


        // Create List button
        // Create List button
        Button(
            onClick = {
                // Validate and create the new list
                if (newListName.text.isNotBlank()) {

                    // assuming I have firebase authentication?
                    val currentUser = Firebase.auth.currentUser
                    val owner = currentUser?.uid ?: "unknown" // Use UID as owner, you can customize this based on your user structure

                    // Log that list is being created
                    Log.d("CreatedList", "Creating list with name: ${newListName.text} by $owner")

                    // Save the new list to Firestore
                    saveListToFirestore(newListName.text, owner)

                    // Notify the caller that a new list is created
                    onListCreated(newListName.text)

                    // Log that callback is invoked
                    Log.d("CreateList", "onListCreated callback invoked")

                    // Navigate back
                    navController.popBackStack()
                } else {
                    // handle case where list name is blank...
                    // show error message...
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Create List")
        }
    }
}

//  func to save new list to Firestore
private fun saveListToFirestore(listName: String, owner: String) {
    val firestore = Firebase.firestore
    // assuming you have a "lists" collection... O_o??
    val listsCollection = firestore.collection("lists")

    val newList = MyList(
        listItem = listName,
        createdAt = Date(),
        items = emptyList(),
        owner = owner // Add the owner information
    )

    // Create a new document with the list name as the document ID
    listsCollection.document(listName)
//        .set(mapOf("name" to listName))
        .set(newList)
        .addOnSuccessListener {
            Log.d("Firestore", "List $listName created successfully by $owner")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error creating list $listName", e)
            // handle error, show msg, etc
        }
}