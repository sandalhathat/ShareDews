package com.example.sharedews

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sharedews.FirestoreOps.deleteListFromFirestore
import com.example.sharedews.ui.theme.ShareDewsTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.getInstance
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun DashboardScreen(navController: NavController) {
    val mAuth = getInstance()
    val currentUser = mAuth.currentUser
    if (currentUser != null && currentUser.isEmailVerified) {
        // User is logged in and email is verified, display dashboard content
        DashboardContent(navController, currentUser)
    } else {
        // User is not logged in or email is not verified, display a message
        AccessRestrictedMessage(navController)
    }
}

@Composable
private fun DashboardContent(navController: NavController, currentUser: FirebaseUser) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var newListName by remember { mutableStateOf(TextFieldValue()) }

        // Firestore
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("lists")

        // Read lists from Firestore
        var lists by remember { mutableStateOf(emptyList<String>()) }

        LaunchedEffect(Unit) {
            try {
                val snapshot = collection.get().await()
                val listNames = snapshot.documents.mapNotNull { it.getString("listName") }

                Log.d("DashboardScreen", "List names from Firestore: $listNames")

                lists = listNames
            } catch (e: Exception) {
                Log.e("DashboardScreen", "Error reading from Firestore: ${e.message}")
                e.printStackTrace()
            }
        }

        // Display lists
        // Display lists
        Text(
            text = "Your Lists:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
//                .background(color = Color.LightGray)
        ) {
            for (listName in lists) {
                DashboardListItem(navController, listName, onListDeleted = {
                    // updated the lists after a list is deleted
                    lists = lists.filter { name -> name != it }
                })
            }
        }

        // Create a new list
        // Create a new list
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = newListName.text,
                onValueChange = {
                    newListName = TextFieldValue(text = it)
                },
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )

            Button(
                onClick = {
                    // Navigate to the CreateListScreen
                    navController.navigate("newList")
                },
                modifier = Modifier
                    .padding(4.dp)
            ) {
                Text(text = "Create List")
            }
        }

        // Logout button
        Button(
            onClick = {
                // log out the user and nav back to the login or reg screen
                AuthManager.signOut()
                navController.navigate("home")
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Log out")
        }
    }
}

@Composable
private fun DashboardListItem(navController: NavController, listName: String, onListDeleted: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
//            .background(color = Color.LightGray)
    ) {
        // Display list name
        Text(
            text = listName,
            modifier = Modifier
//                .fillMaxWidth()
                .weight(1f)
                .padding(4.dp)
                .clickable {
                    navController.navigate("listDetail/$listName")
                },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Button to delete the list
        DeleteListButton(navController, listName) {
            onListDeleted(listName)
        }
    }
}

@Composable
fun DeleteListButton(navController: NavController, listName: String, onListDeleted: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    // Button to delete the list
    Button(
        onClick = {
            coroutineScope.launch {
                deleteListFromFirestore(listName)
                // You might want to refresh the UI or perform other actions after deletion
                onListDeleted()
            }
        },
        modifier = Modifier.padding(4.dp)
    ) {
        Text(text = "Delete")
    }
}

@Composable
@Preview
fun DashboardScreenPreview() {
    ShareDewsTheme {
        val navController = rememberNavController()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            DashboardContent(navController, currentUser)
        }
    }
}
