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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun RegistrationScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
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
                handleRegistration(email, password, passwordConfirmation, scope, navController) { result ->
                    when (result) {
                        is RegistrationResult.Success -> {
                            // Registration successful, navigate to the dashboard
                            navController.navigate("dashboard")
                        }
                        is RegistrationResult.Failure -> {
                            // Registration failed, handle the error
                            errorMessage = result.error
                        }
                    }
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

sealed class RegistrationResult {
    data class Success(val user: FirebaseUser) : RegistrationResult()
    data class Failure(val error: String) : RegistrationResult()
}

fun handleRegistration(
    email: String,
    password: String,
    passwordConfirmation: String,
    scope: CoroutineScope,
    navController: NavController,
    resultCallback: (RegistrationResult) -> Unit
) {
    scope.launch {
        if (isCredentialsValid(email, password) && password == passwordConfirmation) {
            try {
                val auth = Firebase.auth
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val user = authResult.user

                // Check if user is not null and send email verification
                if (user != null) {
                    user.sendEmailVerification().await()

                    // Call the function to add user data to the database
                    addUserDataToDatabase(user, UserData(user.uid, email, ""))

                    resultCallback(RegistrationResult.Success(user))
                } else {
                    resultCallback(RegistrationResult.Failure("Registration failed"))
                }
            } catch (e: Exception) {
                resultCallback(RegistrationResult.Failure(e.message ?: "Registration failed"))
            }
        } else {
            resultCallback(RegistrationResult.Failure("Invalid credentials or password mismatch"))
        }
    }
}


//fun handleRegistration(
//    email: String,
//    password: String,
//    passwordConfirmation: String,
//    scope: CoroutineScope,
//    navController: NavController,
//    resultCallback: (RegistrationResult) -> Unit
//) {
//    scope.launch {
//        if (isCredentialsValid(email, password) && password == passwordConfirmation) {
//            try {
//                val auth = Firebase.auth
//                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
//                val user = authResult.user
//
//                // Check if user is not null and send email verification
//                if (user != null) {
//                    user.sendEmailVerification().await()
//                    resultCallback(RegistrationResult.Success(user))
//                } else {
//                    resultCallback(RegistrationResult.Failure("Registration failed"))
//                }
//            } catch (e: Exception) {
//                resultCallback(RegistrationResult.Failure(e.message ?: "Registration failed"))
//            }
//        } else {
//            resultCallback(RegistrationResult.Failure("Invalid credentials or password mismatch"))
//        }
//    }
//}

fun isCredentialsValid(email: String, password: String): Boolean {
    return email.isNotBlank() && password.length >= 6
}

fun addUserDataToDatabase(user: FirebaseUser, userData: UserData) {
    val db = Firebase.firestore

    // check if user email is verified
    if (user.isEmailVerified) {
        // add data to database
        db.collection("users")
            .document(user.uid)
            .set(userData)
            .addOnSuccessListener {
                // data added successfully
            }
            .addOnFailureListener { e ->
                // handle the error
            }
    } else {
        // handle case where user email is not verified
    }
}