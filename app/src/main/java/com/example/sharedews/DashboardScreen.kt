
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Composable
fun DashboardScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var newListName by remember { mutableStateOf(TextFieldValue()) }

        // Firestore
        val db = FirebaseFirestore.getInstance()
        val collection = db.collection("lists")

        // Read lists from Firestore
        var lists by remember { mutableStateOf(emptyList<String>()) }

        LaunchedEffect(Unit) {
            val snapshot = collection.get().await()
            val listNames = snapshot.documents.mapNotNull { it.getString("listName") }
            lists = listNames
        }

        // Display lists
        Text(
            text = "Your Lists:",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            for (listName in lists) {
                Text(
                    text = listName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable {
                            navController.navigate("list/$listName")
                        },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Create a new list
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = newListName.text,
                onValueChange = {
                    newListName = TextFieldValue(text = it)
                },
                textStyle = LocalTextStyle.current, // Add this line
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
            )

            Button(
                onClick = {
                    // Create a new list in Firestore
                    if (newListName.text.isNotEmpty()) {
                        val newList = mapOf(
                            "listName" to newListName.text,
                            "createdAt" to System.currentTimeMillis()
                        )

                        // Use a coroutine to write to Firestore
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                collection.add(newList).await()
                                withContext(Dispatchers.Main) {
                                    newListName = TextFieldValue()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .padding(4.dp)
            ) {
                Text(text = "Create List")
            }
        }
    }
}

@Preview
@Composable
fun DashboardScreenPreview() {
    DashboardScreen(navController = rememberNavController())
}
