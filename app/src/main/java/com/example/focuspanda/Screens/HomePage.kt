package com.example.focuspanda.Screens

import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.animation.core.animateFloatAsState
import com.example.focuspanda.R

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.focuspanda.CommenSection.MainsCard
import com.example.focuspanda.Data.QuickNavigationIterm
import com.example.focuspanda.Model.QuickNavigate1
import com.example.focuspanda.ui.theme.FocusPandaTheme
import com.example.focuspanda.ui.theme.surfaceVariantLight
import com.google.firebase.auth.FirebaseAuth


@Composable
fun MainScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with Panda logo, Logout button and Profile Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Focus Panda",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logout Button
                    IconButton(
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                            navController.navigate("login") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Existing Profile Icon
                    IconButton(
                        onClick = { navController.navigate("profile") },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.dilshan),
                            contentDescription = "User Profile",
                            modifier = Modifier.size(60.dp)
                        )
                    }
                }
            }

            // Banner Section
            Image(
                painter = painterResource(R.drawable.motivation_qoute),
                contentDescription = "Motivational Quote",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Navigation
            Text(
                text = "Productive study tips",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Start)
            )

            // LazyRow
            ItemList(
                featureList = QuickNavigationIterm().loadQuickNavigationIterm(),
                navController = navController
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Play Sounds Section
            Text(
                text = "Play Ambient Sounds",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Start)
            )
            MusicCard(
                title = "Rain Sounds",
                description = "Relaxing rain sounds for focus",
                audioResId = R.raw.rain_sounds
            )

            MusicCard(
                title = "Forest Sounds",
                description = "Calming forest sounds for study",
                audioResId = R.raw.forest_sounds
            )
            Text(
                text = "Study Tools",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Start)
            )

            // Pomodoro Timer Card
            FeatureCard(
                imageRes = R.drawable.pomodoro,
                title = "Pomodoro Timer",
                description = "Use focused time blocks for productivity.",
                onClick = { navController.navigate("pomodoro") }
            )
            FeatureCard(
                imageRes = R.drawable.flashcards,
                title = "Flash Cards",
                description = "Use active learning method to revise what you have learned .",
                onClick = { navController.navigate("flashCards") }
            )


            // Flashcards Card
            FeatureCard(
                imageRes = R.drawable.to_do_list,
                title = "To do List",
                description = "stay Organized and get more work done.",
                onClick = { navController.navigate("todo") }
            )
            FeatureCard(
                imageRes = R.drawable.motivation, // Make sure to add a weather icon to your drawables
                title = " Daily Motivation",
                description = "Get inspired with motivational quotes to boost your productivity",
                onClick = { navController.navigate("quotes") }
            )
            Text(
                text = "Device Settings",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Start)
            )

            SettingsCard(navController = navController)
        }
    }
}

// Rest of your existing composable functions remain exactly the same:
@Composable
fun FeatureCard(imageRes: Int, title: String, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(150.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(imageRes),
                contentDescription = title,
                modifier = Modifier
                    .size(100.dp)
                    .padding(start = 16.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
@Composable
fun SettingsCard(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(150.dp)
            .clickable { navController.navigate("settings") },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(R.drawable.settings_icon), // Add a settings icon to your drawables
                contentDescription = "Settings",
                modifier = Modifier
                    .size(100.dp)
                    .padding(start = 16.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 16.dp)
            ) {
                Text(
                    text = "Device Settings",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Configure device sensors and connectivity",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ClickableImage(imageRes: Int, description: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = description,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ItemList(featureList: List<QuickNavigate1>, navController: NavController) {
    LazyRow(modifier = Modifier.fillMaxWidth()) {
        items(featureList.take(5)) { item ->
            ItemCard(item, navController)
        }
    }
}

@Composable
fun ItemCard(item: QuickNavigate1, navController: NavController) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 1.1f else 1f, label = "")

    Box(
        modifier = Modifier
            .clickable {
                isPressed = true
                val encodedFeature = item.feature.replace(" ", "_")
                navController.navigate("details/$encodedFeature")
            }
            .graphicsLayer(scaleX = scale, scaleY = scale)
    ) {
        MainsCard(
            imageResourceId = item.imageResId,
            title = item.feature,
            details = item.details,
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }

    LaunchedEffect(isPressed) {
        if (isPressed) {
            isPressed = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController, feature: String?) {
    val decodedFeature = feature?.replace("_", " ")

    val featureDetails = mapOf(
        "PomodoroTimer" to "A Pomodoro Timer uses 25-minute work sessions with 5-minute breaks to enhance focus and productivity. After four sessions, take a 15-30 minute break to prevent burnout.",
        "Flashcards" to "study aids with questions or terms on one side and answers on the other. They enhance memory, aid active recall, and improve learning efficiency.Revise key concepts quickly.",
        "MindMaps" to " visually organize ideas using a central concept with branching nodes. They enhance creativity, memory, and understanding by structuring information in a clear, interconnected way.Retrace your studies effectively.",
        "FocusMode" to "minimizes distractions by blocking notifications and apps, helping users concentrate on tasks. It enhances productivity, reduces interruptions, and promotes deep work.Minimize distractions for deep work."
    )

    val detailText = featureDetails[decodedFeature] ?: "No additional details available."

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = decodedFeature ?: "Feature Detail",
                        color = MaterialTheme.colorScheme.onBackground)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = decodedFeature ?: "Feature",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = detailText, fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.popBackStack() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Back")
                }
            }
        }
    }
}

@Composable
fun MusicCard(title: String, description: String, audioResId: Int) {
    val context = LocalContext.current
    var isPlaying by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(text = title, fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface)

                Text(text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Button(
                onClick = {
                    if (isPlaying) {
                        mediaPlayer?.pause()
                    } else {
                        if (mediaPlayer == null) {
                            mediaPlayer = MediaPlayer.create(context, audioResId)
                        }
                        mediaPlayer?.start()
                    }
                    isPlaying = !isPlaying
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPlaying) Color.Red else Color(0xFF4CAF50),
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(if (isPlaying) "Pause" else "Play")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    FocusPandaTheme {  // Or MaterialTheme if you're using that
        MainScreen(navController = rememberNavController())
    }
}
