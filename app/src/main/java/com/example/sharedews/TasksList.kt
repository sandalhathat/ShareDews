package com.example.sharedews

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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sharedews.ui.theme.ShareDewsTheme

@Composable
fun TasksList(tasks: List<Task>) {
    if (tasks.isEmpty()) {
        Text(text = "No tasks available.")
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {

            items(tasks) { task ->
                // Destructure the item to access its properties
                val taskName = task.taskName
                val taskNotes = task.taskNotes
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
                                text = taskName,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = taskNotes,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun TasksListPreview() {
    val sampleTasks = listOf(
        Task("Task 1", "Description 1"),
        Task("Task 2", "Description 2"),
        Task("Task 3", "Description 3")
    )

    ShareDewsTheme {
        TasksList(tasks = sampleTasks)
    }
}
