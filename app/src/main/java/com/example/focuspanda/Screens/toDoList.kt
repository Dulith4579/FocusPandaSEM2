package com.example.focuspanda.Screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.focuspanda.viewmodels.ToDoViewModel
import com.example.focuspanda.Model.Task



@Composable
fun ToDoListScreen(navController: NavController) {
    val viewModel: ToDoViewModel = viewModel()
    val tasks by viewModel.tasks.collectAsState()
    var isFabVisible by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newTaskTitle by remember { mutableStateOf("") }


    LaunchedEffect(Unit) { isFabVisible = true } // Ensures FAB animates when screen appears

    Scaffold(
        floatingActionButton = {
            AnimatedFAB(isFabVisible = isFabVisible){
               showAddDialog = true
                // TODO: Implement add task functionality
            }
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {


            val isLandscape = maxWidth > maxHeight
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background) //
                    .verticalScroll(scrollState), //
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ToDoListContent(
                    tasks = tasks,
                    onTaskChecked = { task, isChecked ->
                        viewModel.updateTask(task.copy(isCompleted = isChecked))
                    },
                    onDeleteTask = { task ->
                        viewModel.deleteTask(task)
                    }
                )
                BackButton(
                    navController = navController,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        if (showAddDialog) {
            AddTaskDialog(
                title = newTaskTitle,
                onTitleChange = { newTaskTitle = it },
                onDismiss = { showAddDialog = false },
                onConfirm = {
                    if (newTaskTitle.isNotBlank()) {
                        viewModel.addTask(newTaskTitle)
                        newTaskTitle = ""
                        showAddDialog = false
                    }
                })
        }


    }
}

@Composable
fun ToDoListContent(
    tasks: List<Task>,
    onTaskChecked: (Task, Boolean) -> Unit,
    onDeleteTask: (Task) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "To Do List",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Task List
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            if (tasks.isEmpty()) {
                Text(
                    text = "No tasks yet. Add one!",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                tasks.forEach { task ->
                    TaskRow(
                        task = task,
                        onCheckedChange = { isChecked -> onTaskChecked(task, isChecked) },
                        onDelete = { onDeleteTask(task) }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskRow(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE8EAF6), RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = task.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = if (task.isCompleted) Color.Gray else Color.Black,
            modifier = Modifier.weight(1f)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    uncheckedColor = Color.Black,
                    checkedColor = Color(0xFF4CAF50)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

    }
}
@Composable
fun AddTaskDialog(
    title: String,
    onTitleChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Add New Task",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Task description") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = onConfirm,
                        enabled = title.isNotBlank()
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}


@Composable
fun BackButton(navController: NavController, modifier: Modifier = Modifier) {
    Button(
        onClick = {
            navController.navigate("dashboard") {
                popUpTo("dashboard") { inclusive = true }
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50),
            contentColor =  (MaterialTheme.colorScheme.onSecondaryContainer)),
            modifier = modifier.padding(0.1.dp)
    ) {
        Text("Back")
    }
}

//  Animated FAB Function
@Composable
fun AnimatedFAB(isFabVisible: Boolean, onClick: () -> Unit) {
    AnimatedVisibility(
        visible = isFabVisible,
        enter = fadeIn(animationSpec = tween(700)) + slideInVertically(initialOffsetY = { 100 }),
        exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(targetOffsetY = { 100 })
    ) {
        FloatingActionButton(
            onClick = onClick,
            containerColor = Color(0xFF4CAF50),
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Edit, contentDescription = "Add Task")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ToDoListScreenPreview() {
    ToDoListScreen(navController = rememberNavController())
}
