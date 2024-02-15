package com.example.sharedews

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaskViewModel : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> get() = _tasks
    fun setTasks(newTasks: List<Task>) {
        _tasks.value = newTasks
        Log.d("TaskViewModel", "Set tasks: $newTasks")
    }

}