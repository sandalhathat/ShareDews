package com.example.sharedews

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks

    //    fun setTasks(newTasks: List<Task>) {
    fun setTasks(newTasks: MutableList<Task>) {
        _tasks.value = newTasks
        Log.d("TaskViewModel", "Set tasks: $newTasks")
    }


    fun fetchTasks(listDocumentId: String) {
        viewModelScope.launch {
            try {
                val tasks = FirestoreOps.fetchTasksFS(listDocumentId)
                _tasks.value = tasks
                Log.d("TaskViewModel", "Received tasks: $tasks")
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error fetching tasks: ${e.message}")
                e.printStackTrace()
            }
        }
    }


    suspend fun editTask(
        listDocumentId: String,
        taskId: String,
        newTaskName: String,
        newTaskNotes: String
    ) {
        try {
            Log.d(
                "TaskViewModel",
                "Editing task - listDocumentId: $listDocumentId, taskId: $taskId, newTaskName: $newTaskName, newTaskNotes: $newTaskNotes"
            )
            FirestoreOps.editTaskFS(listDocumentId, taskId, newTaskName, newTaskNotes)
            fetchTasks(listDocumentId)
            Log.d("TaskViewModel", "Task edited successfully")
        } catch (e: Exception) {
            Log.e("TaskViewModel", "Error editing task: ${e.message}")
        }
    }


}