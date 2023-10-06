package com.example.sharedews
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
@Composable
fun ListContentScreen(navController: NavController, listName: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "List Content: $listName")
        Spacer(modifier = Modifier.height(16.dp))
        // Add your list content here
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                //add functionality to navigate back to the list management screen
                navController.navigate("listManagement")
            }
        ) {
            Text(text = "Back to List Management")
        }
    }
}