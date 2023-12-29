package com.example.sharedews

//import androidx.compose.material3.icons.Icons
//import androidx.compose.material3.icons.filled.ArrowBack
//import androidx.compose.material3.A
import android.util.Log
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun NewListScreen(navController: NavController) {
    var newListName by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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
            Text(text = stringResource(id = R.string.back)) // You can replace this with your actual string resource
        }

        // List Name input
        BasicTextField(
            value = newListName,
            onValueChange = { newInput -> newListName = newInput },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        )

        // Create List button
        Button(
            onClick = {
                // Validate and create the new list
                if (newListName.text.isNotBlank()) {
                    // Navigate to ListDetailScreen for the newly created list
                    navController.navigate("listDetail/${newListName.text}")
                } else {
                    // handle case where list name is blank..
                    // so show error message...
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Create List")
        }
        Log.d("NewListScreen", "After Button")
    }
}
