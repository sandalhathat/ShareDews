package com.example.sharedews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun AccessRestrictedMessage(navController: NavController) {
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