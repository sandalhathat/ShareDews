package com.example.sharedews

import TaskDetailScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sharedews.FirestoreOps.fetchListDocumentIdFromFirestore
import com.example.sharedews.ui.theme.ShareDewsTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.ktx.appCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        Log.d("FirebaseInit", "Firebase initialized: ${FirebaseApp.getInstance().name}")

        try {
            Firebase.appCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
        } catch (exception: Exception) {
            Log.e("AppCheckError", "Error installing App Check: ${exception.message}")
        }

        setContent {
            ShareDewsTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                LaunchedEffect(Unit) {
                    val authStateListener = FirebaseAuth.AuthStateListener { auth ->
                        val currentUser = auth.currentUser
                        val isEmailVerified = currentUser?.isEmailVerified == true
                        if (currentUser != null && isEmailVerified) {
                            navController.navigate("dashboard")
                        }
                    }
                    auth.addAuthStateListener(authStateListener)
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    content = { padding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp) // Add some padding here
                        ) {
                            NavHost(navController = navController, startDestination = "home") {
                                composable("home") {
                                    Log.d("Navigation", "Navigated to home screen")
                                    HomePage(
                                        navController = navController,
                                        snackbarHostState = snackbarHostState,
                                        lifecycleScope = lifecycleScope
                                    )
                                }

                                composable("dashboard") {
                                    DashboardScreen(navController = navController)
                                }

                                composable("registration") {
                                    RegistrationScreen(navController = navController)
                                }

                                composable("newList") {
                                    CreateListScreen(navController = navController) { newListName ->
                                        Log.d(
                                            "CreateList",
                                            "onListCreated callback received with newListName: $newListName"
                                        )
                                        navController.navigate("listDetail/$newListName")
                                    }
                                }

                                composable("listDetail/{listName}") { backStackEntry ->
                                    val listName =
                                        backStackEntry.arguments?.getString("listName")
                                    var listDocumentId by remember {
                                        mutableStateOf<String?>(
                                            null
                                        )
                                    }
                                    LaunchedEffect(listName) {
                                        listDocumentId =
                                            listName?.let { name ->
                                                fetchListDocumentIdFromFirestore(
                                                    name
                                                )
                                            }
                                    }
                                    ListDetailScreen(
                                        navController,
                                        listName ?: "",
                                        listDocumentId ?: ""
                                    )
//                                    Log.d("SelectList", "testing selection of list to print $listDocumentId")
                                }

                                composable("taskDetail/{listDocumentId}/{taskName}") { backStackEntry ->
                                    val listDocumentId =
                                        backStackEntry.arguments?.getString("listDocumentId")
                                    val taskName = backStackEntry.arguments?.getString("taskName")

                                    TaskDetailScreen(
                                        navController = navController,
                                        listDocumentId = listDocumentId ?: "",
                                        taskName = taskName ?: "",
                                        onEditTask = { _, editedTaskName, _ ->
                                            CoroutineScope(Dispatchers.IO).launch {
                                                try {
                                                    FirestoreOps.editTask(
                                                        listDocumentId ?: "",
                                                        taskName ?: "",
                                                        editedTaskName,
                                                        // You can fetch taskNotes here if needed
                                                        ""
                                                    )
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        }
                                    )
                                }


                            }
                        }
                    }
                )
            }
        }
    }

}