package com.example.focuspanda.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focuspanda.Model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ToDoViewModel : ViewModel() {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        // Initialize with some sample tasks
        viewModelScope.launch {
            _tasks.value = listOf(
                Task(title = "Chemistry 3hr study"),
                Task(title = "Chemistry Tute Work"),
                Task(title = "Chemistry Homework"),
                Task(title = "Biology Paper"),
                Task(title = "Physics Past Questions"),
                Task(title = "Math Assignment"),
                Task(title = "History Essay"),
                Task(title = "Computer Science Project")
            )
        }
    }

    fun addTask(title: String) {
        viewModelScope.launch {
            _tasks.value = _tasks.value + Task(title = title)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.map {
                if (it.id == task.id) task else it
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.filter { it.id != task.id }
        }
    }
}