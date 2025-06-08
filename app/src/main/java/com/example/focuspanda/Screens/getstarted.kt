package com.example.focuspanda.Screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.focuspanda.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.net.URL

@Serializable
data class Introduction(val name: String, val imageUrl: String)

@Serializable
data class DessertsResponse(val desserts: List<Introduction>)

class DessertRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun loadFromGitHub(url: String): List<Introduction> {
        return withContext(Dispatchers.IO) {
            try {
                val jsonString = URL(url).readText()
                val response = json.decodeFromString<DessertsResponse>(jsonString)
                response.desserts
            } catch (e: Exception) {
                getFallback()
            }
        }
    }

    private fun getFallback(): List<Introduction> {
        return try {
            val inputStream = context.assets.open("Fallback.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val jsonString = String(buffer, Charsets.UTF_8)
            val response = json.decodeFromString<DessertsResponse>(jsonString)
            response.desserts
        } catch (e: Exception) {
            emptyList() // fallback failed
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeaturedDrinksPage(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { DessertRepository(context) }

    var desserts by remember { mutableStateOf<List<Introduction>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    val githubJsonUrl =
        "https://raw.githubusercontent.com/Dulith4579/focus_panda_data/refs/heads/main/desserts.json"

    LaunchedEffect(Unit) {
        try {
            desserts = repository.loadFromGitHub(githubJsonUrl)
        } catch (e: Exception) {
            error = e.message
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("How Focus panda Works", fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Failed to load desserts", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Using offline data", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    HorizontalDessertList(desserts = desserts)
                }

            }
            }
        }
    }


@Composable
fun FeaturedCard(dessert: Introduction) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = dessert.imageUrl,
                contentDescription = dessert.name,
                placeholder = painterResource(R.drawable.dilshan),
                error = painterResource(R.drawable.error),
                modifier = Modifier.size(150.dp).clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(dessert.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = colors.onSurface)
                Spacer(modifier = Modifier.height(4.dp))

                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* Handle order click event */ },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                ) {
                    Text("Order Now", color = colors.onPrimary)
                }
            }
        }
    }
}
@Composable
fun HorizontalCard(dessert: Introduction) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier
            .width(250.dp)
            .wrapContentHeight()
            .height(340.dp)
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = dessert.imageUrl,
                contentDescription = dessert.name,
                placeholder = painterResource(R.drawable.error),
                error = painterResource(R.drawable.error),
                modifier = Modifier
                    .height(180.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                dessert.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = colors.onSurface,
                maxLines = 10

            )


        }
    }
}



@Composable
fun HorizontalDessertList(desserts: List<Introduction>) {
    LazyRow(  // This creates the horizontal scrolling container
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 8.dp),
        modifier = Modifier.wrapContentHeight()
    ) {
        items(desserts.size) { index ->
            HorizontalCard(dessert = desserts[index])
        }
    }
}

