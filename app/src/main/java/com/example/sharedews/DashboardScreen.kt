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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
fun DashboardScreen(navController: NavController) {
    val mAuth = FirebaseAuth.getInstance()
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
            .padding(16.dp),
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
                lists = listNames
            } catch (e: Exception) {
                Log.e("DashboardScreen", "Error reading from Firestore: ${e.message}")
                e.printStackTrace()
            }
        }

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
        ) {
            for (listName in lists) {
                Text(
                    text = listName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable {
                            navController.navigate("list/$listName")
                        },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

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
                    try {
                        // Create a new list in Firestore
                        if (newListName.text.isNotEmpty()) {
                            val newList = mapOf(
                                "listName" to newListName.text,
                                "createdAt" to System.currentTimeMillis()
                            )

                            // Use a coroutine to write to Firestore
                            CoroutineScope(Dispatchers.IO).launch {
                                collection.add(newList).await()
                                withContext(Dispatchers.Main) {
                                    newListName = TextFieldValue()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("DashboardScreen", "Error creating list: ${e.message}")
                        e.printStackTrace()
                    }
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
private fun AccessRestrictedMessage(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Dashboard Access Restricted")
        Text(text = "Please log in and verify your email to access the dashboard.")
        Button(
            onClick = {
                // Navigate back to the login or registration screen
                navController.navigate("home")
            }
        ) {
            Text(text = "Go Back")
        }
    }
}
