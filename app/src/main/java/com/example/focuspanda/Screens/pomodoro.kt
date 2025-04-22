package com.example.focuspanda.Screens



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroTimerScreen(navController: NavController) {
    var minutes by remember { mutableStateOf(25) }
    var seconds by remember { mutableStateOf(0) }
    var isRunning by remember { mutableStateOf(false) }
    var showCustomTimeDialog by remember { mutableStateOf(false) }

    // Timer countdown logic
    LaunchedEffect(isRunning) {
        while (isRunning && (minutes > 0 || seconds > 0)) {
            delay(1000L)
            if (seconds == 0) {
                if (minutes > 0) {
                    minutes--
                    seconds = 59
                } else {
                    isRunning = false // Stop timer when it reaches 00:00
                }
            } else {
                seconds--
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isLandscape = maxWidth > maxHeight

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TimerDisplay(minutes, seconds)
            Spacer(modifier = Modifier.height(24.dp))
            ControlButtons(
                isRunning = isRunning,
                onStartPause = { isRunning = !isRunning },
                onReset = {
                    isRunning = false
                    minutes = 25
                    seconds = 0
                },
                onSetCustomTime = { showCustomTimeDialog = true },
                onBack = { navController.popBackStack() }
            )
        }
    }

    if (showCustomTimeDialog) {
        CustomTimeDialog(
            onDismiss = { showCustomTimeDialog = false },
            onConfirm = { customMinutes, customSeconds ->
                minutes = customMinutes
                seconds = customSeconds
                showCustomTimeDialog = false
            }
        )
    }
}

@Composable
fun TimerDisplay(minutes: Int, seconds: Int) {
    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pomodoro Timer",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TimerBox(value = minutes)
            Text(text = ":", fontSize = 40.sp, fontWeight = FontWeight.Bold)
            TimerBox(value = seconds)
        }
    }
}

@Composable
fun TimerBox(value: Int) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(Color(0xFFEDE7F6), shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = value.toString().padStart(2, '0'),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun ControlButtons(
    isRunning: Boolean,
    onStartPause: () -> Unit,
    onReset: () -> Unit,
    onSetCustomTime: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,

        ) {
            Button(
                onClick = onStartPause,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00897B),
                    contentColor =  (MaterialTheme.colorScheme.onSecondaryContainer))
            ) {
                Text(if (isRunning) "Pause" else "Start")
            }
            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF81C784),
                    contentColor =  (MaterialTheme.colorScheme.onSecondaryContainer))
            ) {
                Text("Reset")
            }
        }
        Button(
            onClick = onSetCustomTime,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32),
                contentColor =  (MaterialTheme.colorScheme.onSecondaryContainer))
        ) {
            Text("Set Custom Time")
        }
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50),
                contentColor =  (MaterialTheme.colorScheme.onSecondaryContainer))
        ) {
            Text("Back")
        }
    }
}

@Composable
fun CustomTimeDialog(
    onDismiss: () -> Unit,
    onConfirm: (customMinutes: Int, customSeconds: Int) -> Unit
) {
    var customMinutes by remember { mutableStateOf("") }
    var customSeconds by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    val minutes = customMinutes.toIntOrNull() ?: 0
                    val seconds = customSeconds.toIntOrNull() ?: 0
                    onConfirm(minutes, seconds)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        },
        title = { Text("Set Custom Time") },
        text = {
            Column {
                OutlinedTextField(
                    value = customMinutes,
                    onValueChange = { customMinutes = it },
                    label = { Text("Minutes") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = customSeconds,
                    onValueChange = { customSeconds = it },
                    label = { Text("Seconds") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    )
}

@Preview(showBackground = true, name = "Pomodoro Timer")
@Composable
fun PomodoroTimerScreenPreview() {
    PomodoroTimerScreen(navController = rememberNavController())
}
