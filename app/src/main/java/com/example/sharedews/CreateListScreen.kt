package com.example.sharedews

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sharedews.FirestoreOps.saveListFS
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun CreateListScreen(
    navController: NavController,
    onListCreated: (String) -> Unit
) {
    var userInputListName by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
//            .background(color = Color.White)
    ) {


        // Back button
        // Back button
        Button(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier
                .padding(4.dp)
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            Text(text = stringResource(id = R.string.back))
        }

        // List Name input
        BasicTextField(
            value = userInputListName,
            onValueChange = { newInput -> userInputListName = newInput },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .height(40.dp)
                .background(color = Color.LightGray)
        )


        // Create List button
        // Create List button
        Button(
            onClick = {
                // Validate and create the new list
                if (userInputListName.text.isNotBlank()) {

                    // assuming I have firebase authentication?
                    val currentUser = Firebase.auth.currentUser
                    val owner = currentUser?.uid
                        ?: "unknown" // Use UID as owner, you can customize this based on your user structure

                    // Log that list is being created
                    Log.d("CreatedList", "Creating list with name: ${userInputListName.text} by $owner")

                    // Save the new list to Firestore
                    CoroutineScope(Dispatchers.Main).launch {
//                        saveListToFirestore(userInputListName.text, owner) { listName, listDocumentId ->
//                            onListCreated(listName)
//                        saveListToFirestore(userInputListName.text, owner) { listName ->
//                            onListCreated(listName)
                        saveListFS(userInputListName.text, owner) { result ->
                            onListCreated(result)

                            // Notify the caller that a new list is created
                            onListCreated(userInputListName.text)

                            // Log that callback is invoked
//                            Log.d("CreateList", "onListCreated callback invoked")
                            Log.d("CreateList", "onListCreated callback invoked with result: $result")

                            // Navigate back
                            navController.popBackStack()
                        }
                    }
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

@Composable
@Preview
fun CreateListScreenPreview() {
    // Mock data for preview
//    var newListName by remember { mutableStateOf(TextFieldValue("Mock List")) }

    // Mock NavController
    val navController = rememberNavController()

    // Preview the CreateListScreen
    CreateListScreen(navController = navController, onListCreated = { /* No-op for preview */ })
}
