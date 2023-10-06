package com.example.sharedews

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object AuthManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    suspend fun createUserWithEmailAndPassword(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            auth.createUserWithEmailAndPassword(email, password).await()
        }
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            auth.signInWithEmailAndPassword(email, password).await()
        }
    }

    // sign-out function
    fun signOut() {
        auth.signOut()
    }

    fun isUserSignedIn(): Boolean {
        return auth.currentUser != null
    }
    fun sendEmailVerification(): Task<Void>? {
        val user = auth.currentUser
        return user?.sendEmailVerification()
    }
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    // Add authentication state listener
    fun addAuthStateListener(listener: FirebaseAuth.AuthStateListener) {
        auth.addAuthStateListener(listener)
    }

}
