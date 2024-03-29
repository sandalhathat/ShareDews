package com.example.sharedews

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TasksList(
    tasks: MutableList<Task>,
    onDeleteTask: (String) -> Unit,
    onCompleteTask: (String) -> Unit,
    onEditTask: (String, String, String) -> Unit,
    onTaskClick: (String, String) -> Unit
) {
    // Add a log statement to check the tasks received
    Log.d("TasksList", "Received tasks: $tasks")
    if (tasks.isEmpty()) {
        Text(text = "No tasks available.")
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(mutableStateListOf(*tasks.toTypedArray())) { task ->
                Log.d("TasksList", "Iterating through task: $task")
                // De-structure the item to access its properties
                val taskName = task.taskName
                // Log statement to check iteration through tasks
                Log.d("TasksList", "Iterating through task: $taskName")

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Log statement to check if onTaskClick is invoked
                                Log.d("TasksList", "Clicked on task: $taskName")
                                onTaskClick(task.listDocumentId, task.taskName)
                            }
                            .padding(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = taskName,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // Add an Edit button/icon
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                // Handle the edit action
                                onEditTask(taskName, "", "")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TasksListPreview() {
    val sampleTasks = TestFile.testList.toMutableList()
    TasksList(
        tasks = sampleTasks,
        onDeleteTask = { taskName -> /* Implement onDeleteTask logic */ },
        onCompleteTask = { taskName -> /* Implement onCompleteTask logic */ },
        onEditTask = { taskName, newTaskName, newTaskNotes -> /* Implement onEditTask logic */ },
        onTaskClick = { taskName, taskNotes -> /* Implement onTaskClick logic */ }
    )
}


