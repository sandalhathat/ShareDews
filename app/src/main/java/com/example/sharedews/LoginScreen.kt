package com.example.sharedews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "User Login")
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { newEmail ->
                email = newEmail
            },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { newPassword ->
                password = newPassword
            },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (isCredentialsValid(email, password)) {
                    // Call the composable function inside the LaunchedEffect
                    performLogin(navController, email, password)
                } else {
                    // Handle invalid credentials or display error message
                }
            }
        ) {
            Text(text = "Login")
        }
    }
}

@Composable
fun performLogin(navController: NavController, email: String, password: String) {
    LaunchedEffect(Unit) {
        try {
            val authResult = AuthManager.signInWithEmailAndPassword(email, password)
            navController.navigate("dashboard")
        } catch (e: Exception) {
            // Handle authentication error
        }
    }
}
