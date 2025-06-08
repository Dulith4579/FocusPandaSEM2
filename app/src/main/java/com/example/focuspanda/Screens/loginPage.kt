package com.example.focuspanda.Screens



import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

import com.example.focuspanda.R
import com.example.focuspanda.helper.FirebaseAuthHelper

@Composable
fun LoginScreen(navController: NavController) {
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Centers content
        ) {
            // **Larger Panda Image**
            Image(
                painter = painterResource(id = R.drawable.panda_image),
                contentDescription = "Focus Panda",
                modifier = Modifier
                    .size(200.dp) // **Increased size**
                    .padding(bottom = 10.dp)
            )

            // **Login Form Container**
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xCCFFFFFF)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.elevatedCardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Login",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // **Username Field**
                    OutlinedTextField(
                        value = email.value,
                        onValueChange = { email.value = it },
                        label = { Text("User name",color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // **Password Field with Eye Icon**
                    OutlinedTextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        label = { Text("Password") },
                        visualTransformation = if (passwordVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (passwordVisible.value) R.drawable.open_eye else R.drawable.closed_eye
                                    ),
                                    contentDescription = if (passwordVisible.value) "Hide Password" else "Show Password"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // **Login & Sign-Up Buttons**
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
                                    FirebaseAuthHelper.signInWithEmail(
                                        email = email.value,
                                        password = password.value,
                                        onSuccess = {
                                            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                                            navController.navigate("home") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        },
                                        onFailure = { errorMessage ->
                                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Please enter both email and password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },

                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1B5E20),
                                contentColor =  (MaterialTheme.colorScheme.onSecondaryContainer)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Login")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { navController.navigate("signup") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50),
                                contentColor =  (MaterialTheme.colorScheme.onSecondaryContainer)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Sign Up")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Login Screen Preview", widthDp = 360, heightDp = 640)
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}

