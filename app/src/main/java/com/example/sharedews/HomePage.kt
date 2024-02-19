package com.example.sharedews

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Composable
fun HomePage(navController: NavHostController, snackbarHostState: SnackbarHostState, lifecycleScope: LifecycleCoroutineScope) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var stayLoggedIn by remember { mutableStateOf(false) }
    var showErrorSnackbar by remember { mutableStateOf(false) }

    val auth = Firebase.auth
    val currentUser = auth.currentUser
    val isEmailVerified = currentUser?.isEmailVerified == true

    if (currentUser != null && isEmailVerified) {
        LaunchedEffect(Unit) {
            navController.navigate("dashboard")
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Shared Todo List")

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = username,
            onValueChange = { newUsername ->
                username = newUsername
            },
            label = { Text("Username") },
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = stayLoggedIn,
                onCheckedChange = { checked ->
                    stayLoggedIn = checked
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Stay Logged-In")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isCredentialsValid(username, password)) {
                    lifecycleScope.launch {
                        try {
                            Log.d("Coroutine", "Before executing asynchronous code")
                            val result =
                                AuthManager.signInWithEmailAndPassword(username, password)
                            Log.d("Coroutine", "After executing asynchronous code")

                            if (result.user != null) {
                                navController.navigate("dashboard")
                            } else {
                                Log.e(
                                    "LoginError",
                                    "Authentication failed: Invalid credentials"
                                )
                                showErrorSnackbar = true
                            }
                        } catch (e: FirebaseAuthInvalidCredentialsException) {
                            Log.e("LoginError", "Authentication failed: ${e.message}")
                            showErrorSnackbar = true
                        } catch (e: Exception) {
                            showErrorSnackbar = true
                            Log.e("LoginError", "Authentication failed: ${e.message}")
                        }
                    }
                } else {
                    Log.e(
                        "LoginError",
                        "Invalid credentials: Username should not be blank, password must be at least 6 characters."
                    )
                    showErrorSnackbar = true
                }
            }
        ) {
            Text(text = "Login")
        }

        if (showErrorSnackbar) {
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar("Authentication failed. Please check your credentials.")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("registration")
            }
        ) {
            Text(text = "Create Account")
        }
    }
}