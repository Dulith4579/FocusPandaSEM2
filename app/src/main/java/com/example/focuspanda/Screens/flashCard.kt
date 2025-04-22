package com.example.focuspanda.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Flashcard(var front: String, var back: String)

@Composable
fun FlashcardScreen() {
    var flashcards by remember {
        mutableStateOf(
            listOf(
                Flashcard("Question 1", "Answer 1"),
                Flashcard("Question 2", "Answer 2")
            )
        )
    }
    var selectedCardIndex by remember { mutableStateOf(0) }
    var isFrontVisible by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            // Flashcard
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .weight(1f)
//                    .padding(16.dp)
//                    .clickable { isFrontVisible = !isFrontVisible },
//                shape = RoundedCornerShape(16.dp),
//                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
//            ) {
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .padding(16.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Text(
//                        text = if (isFrontVisible) flashcards[selectedCardIndex].front else flashcards[selectedCardIndex].back,
//                        fontSize = 24.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.Black,
//                        textAlign = TextAlign.Center
//                    )
//                }
//            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        // Edit front
                        val newFront = "Edited Question ${selectedCardIndex + 1}"
                        flashcards = flashcards.toMutableList().apply {
                            this[selectedCardIndex] = this[selectedCardIndex].copy(front = newFront)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90CAF9))
                ) {
                    Text("Edit Front")
                }
                Button(
                    onClick = {
                        // Edit back
                        val newBack = "Edited Answer ${selectedCardIndex + 1}"
                        flashcards = flashcards.toMutableList().apply {
                            this[selectedCardIndex] = this[selectedCardIndex].copy(back = newBack)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90CAF9))
                ) {
                    Text("Edit Back")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        // Previous card
                        if (selectedCardIndex > 0) selectedCardIndex--
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Previous")
                }
                Button(
                    onClick = {
                        // Next card
                        if (selectedCardIndex < flashcards.size - 1) selectedCardIndex++
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Next")
                }
            }
        }

        // Add Flashcard FAB
//        FloatingActionButton(
//            onClick = {
//                // Add new flashcard
//                flashcards = flashcards + Flashcard("New Question", "New Answer")
//                selectedCardIndex = flashcards.lastIndex
//            },
//            modifier = Modifier
//                .align(Alignment.BottomEnd)
//                .padding(16.dp),
//            containerColor = Color(0xFF4CAF50)
//        ) {
//            Icon(Icons.Default.Add, contentDescription = "Add Flashcard")
//        }
//    }
//}
    }
    }
@Preview(showBackground = true)
@Composable
fun FlashcardScreenPreview( ) {
    FlashcardScreen()
}
