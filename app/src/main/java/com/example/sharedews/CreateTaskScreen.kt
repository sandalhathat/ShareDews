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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CreateTaskScreen(navController: NavController, onTaskCreated: (String, String) -> Unit) {
    var newTaskName by remember { mutableStateOf(TextFieldValue()) }
    var newTaskNotes by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = Color.White)
    ) {
        // Task Name input
        BasicTextField(
            value = newTaskName,
            onValueChange = { newInput -> newTaskName = newInput },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .background(color = Color.DarkGray)
        )

        // Task Notes input
        BasicTextField(
            value = newTaskNotes,
            onValueChange = { newInput -> newTaskNotes = newInput },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .background(color = Color.DarkGray)
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
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Create Task")
        }
    }
}
