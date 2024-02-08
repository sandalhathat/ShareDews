package com.example.sharedews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun CreateTaskScreen(navController: NavController, onTaskCreated: (String, String) -> Unit) {
    var newTaskName by remember { mutableStateOf(TextFieldValue()) }
    var newTaskNotes by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = Color.DarkGray)
    ) {
        // Task Name input
        BasicTextField(
            value = newTaskName,
            onValueChange = { newInput -> newTaskName = newInput },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .background(color = Color.LightGray)
        )

        // Task Notes input
        BasicTextField(
            value = newTaskNotes,
            onValueChange = { newInput -> newTaskNotes = newInput },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .background(color = Color.LightGray)
        )

        // Create Task button
        Button(
            onClick = {
                // Validate and create the new task
                if (newTaskName.text.isNotBlank()) {
                    // Notify the caller that a new task is created
                    onTaskCreated(newTaskName.text, newTaskNotes.text)
                    // Navigate back
                    navController.popBackStack()
                } else {
                    // handle case where task name is blank...
                    // show error message...
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Create Task")
        }
    }
}

@Composable
@Preview
fun CreateTaskScreenPreview() {
    // Create a preview NavController (you can use rememberNavController())
    val navController = rememberNavController()

    // Create a preview of your composable
    CreateTaskScreen(navController = navController, onTaskCreated = { taskName, taskNotes ->
        // This block won't be executed in the preview
        // You can leave it empty or add some mock data if needed
    })
}
