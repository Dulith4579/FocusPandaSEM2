package com.example.focuspanda.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.focuspanda.viewmodels.QuotesViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen(navController: NavController) {
    val viewModel: QuotesViewModel = viewModel()
    var currentQuoteIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.fetchQuotes()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Motivational Quotes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Quote Card with scrollable content
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(300.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        viewModel.isLoading.value -> CircularProgressIndicator()
                        viewModel.quotes.value.isNotEmpty() -> {
                            Column(
                                modifier = Modifier
                                    .verticalScroll(rememberScrollState())
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "\"${viewModel.quotes.value[currentQuoteIndex].quote}\"",
                                    style = MaterialTheme.typography.headlineSmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )

                                Text(
                                    text = "- ${viewModel.quotes.value[currentQuoteIndex].author}",
                                    fontStyle = FontStyle.Italic,
                                    style = MaterialTheme.typography.bodyLarge
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Category: ${viewModel.quotes.value[currentQuoteIndex].category}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                        viewModel.error.value != null -> {
                            Text(
                                text = viewModel.error.value!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        else -> Text("No quotes available")
                    }
                }
            }

            // Navigation buttons if multiple quotes
            if (viewModel.quotes.value.size > 1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            currentQuoteIndex =
                                (currentQuoteIndex - 1).mod(viewModel.quotes.value.size)
                        },
                        enabled = viewModel.quotes.value.size > 1,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ArrowBack, "Previous")
                    }

                    Text(
                        text = "${currentQuoteIndex + 1}/${viewModel.quotes.value.size}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = {
                            currentQuoteIndex =
                                (currentQuoteIndex + 1).mod(viewModel.quotes.value.size)
                        },
                        enabled = viewModel.quotes.value.size > 1,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ArrowForward, "Next")
                    }
                }
            }

            // Action Buttons
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.fetchQuotes()
                        currentQuoteIndex = 0
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Load New Quotes")
                }

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Text("Back to Home")
                }
            }
        }
    }
}