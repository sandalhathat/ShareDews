package com.example.sharedews

import android.util.Log
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sharedews.FirestoreOps.completeTask
import com.example.sharedews.FirestoreOps.deleteTask
import com.example.sharedews.FirestoreOps.editTask
import com.example.sharedews.FirestoreOps.updateListNameInFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ListDetailScreen(navController: NavController, listName: String, listDocumentId: String) {
    var newListName by remember { mutableStateOf(listName) }
    var isEditing by remember { mutableStateOf(false) }
    var isCreateTaskSheetVisible by remember { mutableStateOf(false) }
    val taskViewModel: TaskViewModel = viewModel()
    val tasks by taskViewModel.tasks.observeAsState()
    var selectedTask: Task? by remember { mutableStateOf(null) }

    if (listDocumentId.isNotEmpty()) {
        LaunchedEffect(listDocumentId) {
            taskViewModel.fetchTasks(listDocumentId)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
//            .background(color = Color.LightGray)
    ) {

        // back button
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
                        .background(color = Color.LightGray)
                )
            } else {
                Text(
                    text = newListName,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(4.dp)
                )
            }

            // edit button?
            // edit button?
            IconButton(
                onClick = {
                    if (isEditing) {
                        // Save changes to Firestore
                        val finalNewListName = newListName // create a final var?
                        CoroutineScope(Dispatchers.IO).launch {
                            try {

                                updateListNameInFirestore(listDocumentId, finalNewListName)
//                                updateListNameInFirestore(listName, finalNewListName)
                                withContext(Dispatchers.Main) {
                                    isEditing = false
                                    Log.d(
                                        "ListDetailScreen",
                                        "List name updated successfully to: $finalNewListName"
                                    )
                                }
                            } catch (e: Exception) {
                                Log.e("ListDetailScreen", "Error updating list name: ${e.message}")
                                e.printStackTrace()
                            }
                        }
                    } else {
                        isEditing = true
                    }
                },
                modifier = Modifier
                    .padding(4.dp)
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
        // call function from taskslist composable where you render the list of tasks
        TasksList(
            tasks ?: emptyList(),
            onDeleteTask = { taskName: String ->
                CoroutineScope(Dispatchers.IO).launch { deleteTask(listDocumentId, taskName) }
            },
            onCompleteTask = { taskName: String ->
                CoroutineScope(Dispatchers.IO).launch { completeTask(listDocumentId, taskName) }
            },
            onEditTask = { taskName: String, newTaskName: String, newTaskNotes: String ->
                CoroutineScope(Dispatchers.IO).launch {
                    editTask(listDocumentId, taskName, newTaskName, newTaskNotes)
                }
            },
            onTaskClick = { taskName: String, taskNotes: String ->
                selectedTask =
                    Task(
                        taskName = taskName,
                        taskNotes = taskNotes,
                        listDocumentId = listDocumentId
                    )
                navController.navigate("taskDetail/$listDocumentId/$taskName")
            }
        )
        Log.d("ListDetailScreen", "Rendering tasks: $tasks")


        // Button to add a new task
        // Button to add a new task
        Button(
            onClick = {
                // open bottom sheet to create new task
                isCreateTaskSheetVisible = true
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
                listDocumentId = listDocumentId, // Pass the listDocumentId parameter
                lifecycleOwner = LocalLifecycleOwner.current,
                onTaskCreated = { taskName, taskNotes ->
                    // add new task to list
                    taskViewModel.setTasks(
                        tasks.orEmpty() + Task(
                            taskName,
                            taskNotes,
                            false,
                            listDocumentId
                        )
                    )
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

    // Log the listDocumentId to check its value during preview
    Log.d("ListDetailScreenPreview", "listDocumentId: YourListDocumentId")
    // Create a preview of your composable
    ListDetailScreen(
        navController = navController, listName = "Preview List,\nPreview List,\n" +
                "Preview List,\n" +
                "Preview List,\n" +
                "Preview List", listDocumentId = "previewListId"
    )
}
