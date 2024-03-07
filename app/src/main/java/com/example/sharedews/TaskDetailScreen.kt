
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import com.example.sharedews.TestFile
import com.example.sharedews.ui.theme.ShareDewsTheme

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

        // Task Name Row with Edit Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Display task name
            Text(
                text = "$taskName",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )

            // Edit button for Task Name
            IconButton(
                onClick = {
                    // Handle Task Name edit logic here
                    // You can set isEditing to true, similar to how it's done for task notes
                },
                modifier = Modifier
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Display task notes
        taskNotes?.let {

            // Edit Button for Task Notes
            IconButton(
                onClick = {
                    // Handle Task Notes edit logic here
                    isEditing = !isEditing
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.Done else Icons.Default.Edit,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }



            Text(
                text = "Task Notes: $it",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(4.dp)
            )
        }



        // Text field for editing task name and task notes
        if (isEditing) {
            // Customize this part based on how you want the editing UI to look
            TextField(
                value = editedTaskName,
                onValueChange = { editedTaskName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Text field for editing task notes
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
@Preview(apiLevel = 33)
fun TaskDetailScreenPreview() {
    val navController = rememberNavController()

    // Initialize the testList to force lazy initialization
    val testList = TestFile.testList

    // Create the preview
    ShareDewsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Check if testList is not empty before accessing its first item
            if (testList.isNotEmpty()) {
                val task = testList[0]
                TaskDetailScreen(
                    navController = navController,
                    taskName = task.taskName,
                    taskNotes = task.taskNotes,
                    listDocumentId = task.listDocumentId,
                    onEditTask = { _, _, _ -> /* Implement the onEditTask logic here */ }
                )
            } else {
                Text("Test list is empty.")
            }
        }
    }
}

