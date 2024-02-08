package com.example.sharedews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sharedews.FirestoreOperations.fetchTasksFromFirestore
import com.example.sharedews.FirestoreOperations.updateListNameInFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ListDetailScreen(navController: NavController, listName: String) {
    var newListName by remember { mutableStateOf(listName) }
    var isEditing by remember { mutableStateOf(false) }
    // state to manage bottom sheet visibility
    var isCreateTaskSheetVisible by remember { mutableStateOf(false) }
    // func to open bottom sheet
    fun openCreateTaskSheet() {
        isCreateTaskSheetVisible = true
    }

    // Create a mutable list of list tasks
    var tasks by remember { mutableStateOf(emptyList<Task>()) }

    LaunchedEffect(true) {
        tasks = fetchTasksFromFirestore(listName)
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
                    modifier = Modifier
                        .padding(4.dp)
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

        // Render the list of tasks
        TasksList(tasks)

        // Button to add a new task
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

        // bottom sheet to create a new task
        if (isCreateTaskSheetVisible) {
            CreateTaskScreen(
                navController = navController,
                onTaskCreated = { taskName, taskNotes ->
                    // add new task to list
                    tasks = tasks.toMutableList() + Task(taskName, taskNotes)
                    // close bottom sheet
                    isCreateTaskSheetVisible = false
                }
            )
        }
    }
}

@Composable
@Preview
fun ListDetailScreenPreview() {
    // Create a preview NavController (you can use rememberNavController())
    val navController = rememberNavController()

    // Create a preview of your composable
    ListDetailScreen(navController = navController, listName = "Preview List")
}
