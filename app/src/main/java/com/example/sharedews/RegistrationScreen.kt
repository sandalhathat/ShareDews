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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

@Composable
fun RegistrationScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) } // Store the error message

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "User Registration")

        Spacer(modifier = Modifier.height(16.dp))

        // Email field
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

        // Password field
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

        // Password confirmation field
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

        // Registration button
        Button(
            onClick = {
                if (isCredentialsValid(email, password) && password == passwordConfirmation) {
                    // Clear any previous error message
                    errorMessage = null

                    // Call registration function from AuthManager
                    val authTask: Task<AuthResult> = AuthManager.signInWithEmailAndPassword(email, password)
                    authTask.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // reg successful, nav to dashboard
                            navController.navigate("dashboard")
                        } else {
                            // Registration failed, set the error message
                            errorMessage = task.exception?.message ?: "Registration failed"
                        }
                    }

                } else {
                    // Handle invalid credentials or password mismatch
                    errorMessage = "Invalid credentials or password mismatch"
                }
            }
        ) {
            Text(text = "Register")
        }

        // Display error message if there is one
        errorMessage?.let { message ->
            Text(text = message, color = Color.Red)
        }
    }

}

@Preview
@Composable
fun RegistrationScreenPreview() {
    RegistrationScreen(navController = rememberNavController())
}