package com.example.focuspanda.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.focuspanda.R
import com.example.focuspanda.ui.theme.FocusPandaTheme

data class Flashcard(val id: Int, val front: String, val back: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardScreen(
    navController: NavController? = null,
    modifier: Modifier = Modifier
) {
    // Use rememberSaveable to maintain state across configuration changes
    var flashcards by rememberSaveable { mutableStateOf(emptyList<Flashcard>()) }
    var selectedCardIndex by rememberSaveable { mutableStateOf(0) }
    var isFrontVisible by rememberSaveable { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Initialize with sample data if empty
    LaunchedEffect(Unit) {
        if (flashcards.isEmpty()) {
            flashcards = listOf(
                Flashcard(1, "What is Kotlin?", "A statically typed programming language"),
                Flashcard(2, "What is Composable?", "A function that describes part of your UI"),
                Flashcard(3, "What is Jetpack Compose?", "A modern UI toolkit for Android")
            )
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.flashcards_title),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back_button),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            if (flashcards.isNotEmpty()) { // Only show FAB if there are cards
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.padding(bottom = 80.dp) // Add space for buttons
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_flashcard)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (flashcards.isEmpty()) {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.no_flashcards),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showAddDialog = true }
                    ) {
                        Text(stringResource(R.string.add_first_flashcard))
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Flashcard content
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clickable { isFrontVisible = !isFrontVisible },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isFrontVisible)
                                    flashcards[selectedCardIndex].front
                                else
                                    flashcards[selectedCardIndex].back,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(32.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Navigation buttons at the bottom
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp), // Add padding at bottom
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                if (selectedCardIndex > 0) selectedCardIndex--
                                isFrontVisible = true
                            },
                            enabled = selectedCardIndex > 0,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.previous))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { isFrontVisible = !isFrontVisible },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                if (isFrontVisible) stringResource(R.string.show_answer)
                                else stringResource(R.string.show_question)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (selectedCardIndex < flashcards.size - 1) selectedCardIndex++
                                isFrontVisible = true
                            },
                            enabled = selectedCardIndex < flashcards.size - 1,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(stringResource(R.string.next))
                        }
                    }
                }
            }
        }

        // Add Flashcard Dialog
        if (showAddDialog) {
            var newFront by remember { mutableStateOf("") }
            var newBack by remember { mutableStateOf("") }

            AlertDialog(
                onDismissRequest = {
                    showAddDialog = false
                    newFront = ""
                    newBack = ""
                },
                title = { Text(stringResource(R.string.add_new_flashcard)) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newFront,
                            onValueChange = { newFront = it },
                            label = { Text(stringResource(R.string.question)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newBack,
                            onValueChange = { newBack = it },
                            label = { Text(stringResource(R.string.answer)) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newFront.isNotBlank() && newBack.isNotBlank()) {
                                val newId = (flashcards.maxOfOrNull { it.id } ?: 0) + 1
                                flashcards = flashcards + Flashcard(newId, newFront, newBack)
                                selectedCardIndex = flashcards.size - 1
                                newFront = ""
                                newBack = ""
                                showAddDialog = false
                            }
                        },
                        enabled = newFront.isNotBlank() && newBack.isNotBlank()
                    ) {
                        Text(stringResource(R.string.add))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showAddDialog = false
                            newFront = ""
                            newBack = ""
                        }
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FlashcardScreenPreview() {
    FocusPandaTheme {
        FlashcardScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, showSystemUi = true, widthDp = 640, heightDp = 360)
@Composable
fun FlashcardScreenLandscapePreview() {
    FocusPandaTheme {
        FlashcardScreen(navController = rememberNavController())
    }
}