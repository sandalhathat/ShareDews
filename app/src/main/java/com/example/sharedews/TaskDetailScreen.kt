import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sharedews.ui.theme.ShareDewsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TaskDetailScreen(
    navController: NavController,
    listDocumentId: String,
    taskName: String,
    taskNotes: String? = null,
    onEditTask: (String, String, String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedTaskName by remember { mutableStateOf(taskName) }
    var editedTaskNotes by remember { mutableStateOf(taskNotes.orEmpty()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Back button
        IconButton(
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier
                .padding(4.dp)
                .background(color = Color.Magenta)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
            )
        }

        // Display task notes
        taskNotes?.let {
            Text(
                text = "Task Notes: $it",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(4.dp)
            )
        }

        // Box for edit button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 8.dp, end = 8.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            // Edit/Save button
            IconButton(
                onClick = {
                    if (isEditing) {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                onEditTask(listDocumentId, editedTaskName, editedTaskNotes)
                                withContext(Dispatchers.Main) {
                                    isEditing = false
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    } else {
                        isEditing = true
                    }
                },
                modifier = Modifier.padding(4.dp)
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.Done else Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // text field for editing task name
        if (isEditing) {
            // you can customize this part based on how you want the editing ui to look
            TextField(
                value = editedTaskName,
                onValueChange = { editedTaskName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // text field for editing task notes
            TextField(
                value = editedTaskNotes,
                onValueChange = { editedTaskNotes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

        }


    }
}

@Composable
@Preview
fun TaskDetailScreenPreview() {
    val navController = rememberNavController()

    // Example task details for preview
    val taskName = "sample derp"
    val taskNotes = "sample mcderp, mow lawn, shave derp, " +
            "eat derp, derpy derp"
    val onEditTask: (String, String, String) -> Unit =
        { _, _, _ -> /* Implement the onEditTask logic here */ }

    // Create the preview
    ShareDewsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            TaskDetailScreen(
                navController = navController,
                taskName = taskName,
                taskNotes = taskNotes,
                listDocumentId = "derpyList",
                onEditTask = onEditTask
            )
        }
    }
}
