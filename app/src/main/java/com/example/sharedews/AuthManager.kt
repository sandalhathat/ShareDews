package com.example.sharedews

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

object AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

        // function to create a new user with email and password
    fun createUserWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // user creation successful
                    val user = auth.currentUser
                    // handle success
                } else {
                    // user creation failed
                    val exception = task.exception
                }
            }
    }

    // function to sign in an existing user with email and pass
    fun signInWithEmailAndPassword(email: String, password: String): Task<AuthResult> {
//        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    // sign-in successful
//                    val user = auth.currentUser
//                } else {
//                    // sign in failed
//                    val exception = task.exception
//                    // handle failure... like error msg, but later
//                }
//            }
        return auth.signInWithEmailAndPassword(email, password)
    }

    // sign-out function
    fun signOut() {
        auth.signOut()
    }

    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }

}