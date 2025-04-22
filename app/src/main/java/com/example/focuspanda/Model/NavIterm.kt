package com.example.focuspanda.Model

import androidx.compose.ui.graphics.vector.ImageVector

//data classes holding repeated values
data class NavIterm(
    val label : String,
    val icon : ImageVector,
)



data class Task(
    val id: Int,
    val title: String,
    val isCompleted: Boolean = false
)
data class Flashcard(
    var front: String,
    var back: String
)



