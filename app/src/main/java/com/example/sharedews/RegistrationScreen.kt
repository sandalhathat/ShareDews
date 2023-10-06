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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun RegistrationScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "User Registration")
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

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = passwordConfirmation,
            onValueChange = { newPasswordConfirmation ->
                passwordConfirmation = newPasswordConfirmation
            },
            label = { Text("Confirm Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isCredentialsValid(email, password) && password == passwordConfirmation) {
                    errorMessage = null
                    LaunchedEffect(Unit) {
                        try {
                            val authResult = AuthManager.createUserWithEmailAndPassword(email, password)
                            navController.navigate("dashboard")
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Registration failed"
                        }
                    }
                } else {
                    errorMessage = "Invalid credentials or password mismatch"
                }
            }
        ) {
            Text(text = "Register")
        }

        errorMessage?.let { message ->
            Text(text = message, color = Color.Red)
        }
    }
}