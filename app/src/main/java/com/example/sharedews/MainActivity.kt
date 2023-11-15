package com.example.sharedews

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sharedews.ui.theme.ShareDewsTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.ktx.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth = Firebase.auth

    //    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        FirebaseApp.initializeApp(this)
//        Log.d("FirebaseInit", "Firebase initialized: ${FirebaseApp.getInstance().name}")
//        Firebase.appCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance(),)
//            .addOnFailureListener { exception ->
//                Log.e("AppCheckError", "Error installing App Check: ${exception.message}")
//            }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        Log.d("FirebaseInit", "Firebase initialized: ${FirebaseApp.getInstance().name}")

        try {
            Firebase.appCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
        } catch (exception: Exception) {
            Log.e("AppCheckError", "Error installing App Check: ${exception.message}")
        }

        // ... rest of your code
//}

        setContent {
            ShareDewsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomePage(navController = navController)
                        }
                        composable("dashboard") {
                            DashboardScreen(navController = navController)
                        }
                        composable("registration") {
                            RegistrationScreen(navController = navController)
                        }
                    }
                    val authStateListener = FirebaseAuth.AuthStateListener { auth ->
                        val currentUser = auth.currentUser
                        val isEmailVerified = currentUser?.isEmailVerified == true

                        if (currentUser != null && isEmailVerified) {
                            navController.navigate("dashboard")
                        }
                    }
                    auth.addAuthStateListener(authStateListener)
                }
            }
        }
    }

    fun isCredentialsValid(username: String, password: String): Boolean {
        return username.isNotBlank() && password.length >= 6
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HomePage(navController: NavHostController) {
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
                                val result =
                                    AuthManager.signInWithEmailAndPassword(username, password)
                                if (result.user != null) {
                                    navController.navigate("dashboard")
                                } else {
                                    // Log invalid credentials
                                    Log.e(
                                        "LoginError",
                                        "Authentication failed: Invalid credentials"
                                    )
                                    showErrorSnackbar = true
                                }
                            } catch (e: FirebaseAuthInvalidCredentialsException) {
                                // Handle case where the user is not authenticated even though no exception was thrown
                                Log.e("LoginError", "Authentication failed: ${e.message}")
                                showErrorSnackbar = true
                            } catch (e: Exception) {
                                showErrorSnackbar = true
                                Log.e("LoginError", "Authentication failed: ${e.message}")
                            }
                        }
                    } else {
                        // Handling invalid credentials
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
                // You can display a Snackbar or some other UI element to show the error message.
                Text(text = "Authentication failed. Please check your credentials.")
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
}