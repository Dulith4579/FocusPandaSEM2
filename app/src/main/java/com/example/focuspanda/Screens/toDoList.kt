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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.focuspanda.Model.Task
import com.example.focuspanda.R


@Composable
fun ToDoListScreen(navController: NavController) {
    var isFabVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { isFabVisible = true } // Ensures FAB animates when screen appears

    Scaffold(
        floatingActionButton = {
            AnimatedFAB(isFabVisible = isFabVisible) {
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
                ToDoListContent()
                BackButton(navController = navController, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}

@Composable
fun ToDoListContent() {
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

        //  To-Do Items
        val exampleTasks = listOf(
            "Chemistry 3hr study",
            "Chemistry Tute Work",
            "Chemistry Homework",
            "Biology Paper",
            "Physics Past Questions",
            "Math Assignment",
            "History Essay",
            "Computer Science Project"
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            exampleTasks.forEach { task ->
                TaskRowUIOnly(task)
            }
        }
    }
}

@Composable
fun TaskRowUIOnly(task: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE8EAF6), RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = task,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = false,
                onCheckedChange = null, // Placeholder for non functionality
                colors = CheckboxDefaults.colors(
                    uncheckedColor = Color.Black,
                    checkedColor = Color(0xFF4CAF50)
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Task",
                tint = Color.Red,
                modifier = Modifier.size(24.dp)
            )
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
