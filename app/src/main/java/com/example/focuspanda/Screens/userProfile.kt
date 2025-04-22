package com.example.focuspanda.Screens



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.focuspanda.R

@Composable
fun UserProfileScreen(navController: NavController) {
    Scaffold(
        content = { paddingValues ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
            ) {
                val isLandscape = maxWidth > maxHeight

                Column(modifier = Modifier.fillMaxSize()) {
                    // Top section with title and Back button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() }, // Navigate back
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "User Profile",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    if (isLandscape) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProfileImageSection()
                            ProfileDetailsSection()
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ProfileImageSection()
                            Spacer(modifier = Modifier.height(16.dp))
                            ProfileDetailsSection()
                        }
                    }
                }

                FloatingButtonSection(modifier = Modifier.align(Alignment.BottomCenter))
            }
        }
    )
}


@Composable
fun ProfileImageSection() {
    Image(
        painter = painterResource(id = R.drawable.dilshan),
        contentDescription = "Profile Picture",
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .border(2.dp, Color.Gray, CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ProfileDetailsSection() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ProfileDetailRow(label = "User name", value = "Dilshan")
            ProfileDetailRow(label = "Email", value = "jjk@gmail.com")
            ProfileDetailRow(label = "Phone number", value = "0712345676")
            ProfileDetailRow(label = "Password", value = "*********")
        }
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = value, fontSize = 18.sp)
    }
}

@Composable
fun FloatingButtonSection(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(onClick = { /* Edit Action */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B
        ))) {
            Text(text = "Edit", color =  (MaterialTheme.colorScheme.onSecondaryContainer))
        }
        Button(onClick = { /* Delete Action */ }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
            Text(text = "Delete", color =  (MaterialTheme.colorScheme.onSecondaryContainer))
        }
    }
}
@Composable
fun BackButton(navController: NavController) {
    Button(
        onClick = {
            navController.navigate("dashboard") {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = false // Ensures that "dashboard" stays in the back stack
                }
                launchSingleTop = true // Prevents multiple instances of the same screen
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
        modifier = Modifier.padding(8.dp)
    ) {
        Text(text = "Back", color = Color.White)
    }
}
//@Composable
//fun SignOutButton(navController: NavController) {
//    Button(
//        onClick = {
//            navController.navigate("login") {
//                popUpTo(navController.graph.startDestinationId) {
//                    inclusive = true // Clears the back stack to prevent navigation back to the current screen
//                }
//                launchSingleTop = true // Prevents multiple instances of the login screen
//            }
//        },
//        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
//        modifier = Modifier.padding(8.dp)
//    ) {
//        Text(text = "Sign Out", color = Color.White)
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun UserProfileScreenPreview() {
//    UserProfileScreen(navController = object : NavController(null) {})
//}