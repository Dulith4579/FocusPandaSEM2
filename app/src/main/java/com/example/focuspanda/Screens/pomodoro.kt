package com.example.focuspanda.Screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.focuspanda.viewmodels.CompletedSession
import com.example.focuspanda.viewmodels.PomodoroViewModel
import kotlin.math.abs
import kotlin.math.sqrt



@Composable
fun HistoryDialog(
    completedSessions: List<CompletedSession>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Completed Sessions") },
        text = {
            if (completedSessions.isEmpty()) {
                Text("No sessions completed yet")
            } else {
                LazyColumn {
                    items(completedSessions) { session ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = session.duration,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (session.wasSuccessful) "✓" else "✗",
                                    color = if (session.wasSuccessful) Color.Green else Color.Red
                                )
                            }
                            Text(
                                text = session.date,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun CustomTimeDialog(
    onDismiss: () -> Unit,
    onConfirm: (customMinutes: Int, customSeconds: Int) -> Unit
) {
    var customMinutes by remember { mutableStateOf("") }
    var customSeconds by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
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
            Button(onClick = onDismiss) {
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
    onBack: () -> Unit,
    onShowHistory: () -> Unit
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00897B),
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text(if (isRunning) "Pause" else "Start")
            }
            Button(
                onClick = onReset,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF81C784),
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Reset")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onSetCustomTime,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Text("Custom Time")
            }
            Button(
                onClick = onShowHistory,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5C6BC0),
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(Icons.Default.List, contentDescription = "History")
            }
        }

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50),
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Text("Back")
        }
    }
}

@Composable
fun ProximityVisualization(
    currentValue: Float,
    maxValue: Float,
    isActive: Boolean,
    maxRange: Float,
    modifier: Modifier = Modifier
) {
    val normalizedCurrent = if (maxRange > 0) currentValue / maxRange else 0f
    val normalizedMax = if (maxRange > 0) maxValue / maxRange else 0f

    val proximityText = if (isActive) {
        "Proximity: ${"%.2f".format(currentValue)} cm (${"%.0f".format((1 - normalizedCurrent) * 100)}% near)"
    } else {
        "Proximity sensor inactive"
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = proximityText,
            fontSize = 16.sp,
            color = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(15.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(1 - normalizedCurrent)
                    .fillMaxHeight()
                    .background(
                        color = when {
                            !isActive -> Color.Gray
                            normalizedCurrent < 0.3 -> Color.Red
                            normalizedCurrent < 0.6 -> Color.Yellow
                            else -> Color.Green
                        },
                        shape = RoundedCornerShape(15.dp)
                    )
            )

            if (maxValue > 0 && isActive) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(1 - normalizedMax)
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(Color.Black.copy(alpha = 0.7f))
                )
            }
        }

        if (isActive) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Near", fontSize = 10.sp, color = Color.Gray)
                Text("Far", fontSize = 10.sp, color = Color.Gray)
            }

            if (maxValue > 0) {
                Text(
                    text = "Max: ${"%.2f".format(maxValue)} cm",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun MotionVisualization(
    currentValue: Float,
    maxValue: Float,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val normalizedValue = if (maxValue > 0) (currentValue / maxValue).coerceIn(0f, 1f) else 0f
    val motionText = if (isActive) "Motion: ${"%.2f".format(currentValue)}" else "Motion sensors inactive"

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = motionText,
            fontSize = 16.sp,
            color = if (isActive) MaterialTheme.colorScheme.primary else Color.Gray
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(15.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(normalizedValue)
                    .fillMaxHeight()
                    .background(
                        color = when {
                            !isActive -> Color.Gray
                            normalizedValue > 0.7f -> Color.Red
                            normalizedValue > 0.4f -> Color.Yellow
                            else -> Color.Green
                        },
                        shape = RoundedCornerShape(15.dp)
                    )
            )

            if (maxValue > 0 && isActive) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(normalizedValue)
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(Color.Black.copy(alpha = 0.7f))
                )
            }
        }

        if (isActive) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Low", fontSize = 10.sp, color = Color.Gray)
                Text("Medium", fontSize = 10.sp, color = Color.Gray)
                Text("High", fontSize = 10.sp, color = Color.Gray)
            }

            if (maxValue > 0) {
                Text(
                    text = "Max: ${"%.2f".format(maxValue)}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroTimerScreen(
    navController: NavController,
    viewModel: PomodoroViewModel = viewModel()
) {
    val minutes by viewModel.minutes.collectAsState()
    val seconds by viewModel.seconds.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    val useProximitySensor by viewModel.useProximitySensor.collectAsState()
    val useMotionSensors by viewModel.useMotionSensors.collectAsState()
    val phoneMoved by viewModel.phoneMoved.collectAsState()
    val showMovementWarning by viewModel.showMovementWarning.collectAsState()
    val currentMotionValue by viewModel.currentMotionValue.collectAsState()
    val maxMotionValue by viewModel.maxMotionValue.collectAsState()
    val proximityValue by viewModel.proximityValue.collectAsState()
    val maxProximityValue by viewModel.maxProximityValue.collectAsState()
    val completedSessions by viewModel.completedSessions.collectAsState()
    val showCustomTimeDialog by viewModel.showCustomTimeDialog.collectAsState()
    val showHistoryDialog by viewModel.showHistoryDialog.collectAsState()

    val context = LocalContext.current
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    val sensorListener = remember {
        object : SensorEventListener {
            private var lastAcceleration = 0f
            private var currentAcceleration = 0f

            override fun onSensorChanged(event: SensorEvent?) {
                if (!isRunning) return

                event?.let {
                    when (event.sensor.type) {
                        Sensor.TYPE_PROXIMITY -> {
                            if (useProximitySensor) {
                                val value = event.values[0]
                                viewModel.updateProximityValue(value)
                            }
                        }
                        Sensor.TYPE_ACCELEROMETER -> {
                            if (useMotionSensors) {
                                val x = event.values[0]
                                val y = event.values[1]
                                val z = event.values[2]

                                lastAcceleration = currentAcceleration
                                currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
                                val delta = abs(currentAcceleration - lastAcceleration)

                                viewModel.updateMotionValue(delta * 10)
                            }
                        }
                        Sensor.TYPE_GYROSCOPE -> {
                            if (useMotionSensors) {
                                val rotationX = abs(event.values[0])
                                val rotationY = abs(event.values[1])
                                val rotationZ = abs(event.values[2])

                                val motionValue = (rotationX + rotationY + rotationZ) * 5
                                viewModel.updateMotionValue(motionValue)
                            }
                        }
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(isRunning, useProximitySensor, useMotionSensors) {
        if (isRunning) {
            if (useProximitySensor) {
                val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
                proximitySensor?.let {
                    sensorManager.registerListener(
                        sensorListener,
                        it,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
                }
            }

            if (useMotionSensors) {
                val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

                accelerometer?.let {
                    sensorManager.registerListener(
                        sensorListener,
                        it,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
                }

                gyroscope?.let {
                    sensorManager.registerListener(
                        sensorListener,
                        it,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
                }
            }
        } else {
            sensorManager.unregisterListener(sensorListener)
        }

        onDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isLandscape = maxWidth > maxHeight

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TimerDisplay(minutes, seconds)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Proximity Sensor", fontSize = 12.sp)
                            Switch(
                                checked = useProximitySensor,
                                onCheckedChange = { viewModel.toggleProximitySensor(it) },
                                enabled = !isRunning
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Motion Sensors", fontSize = 12.sp)
                            Switch(
                                checked = useMotionSensors,
                                onCheckedChange = { viewModel.toggleMotionSensors(it) },
                                enabled = !isRunning
                            )
                        }
                    }

                    ControlButtons(
                        isRunning = isRunning,
                        onStartPause = { viewModel.toggleTimer() },
                        onReset = { viewModel.resetTimer() },
                        onSetCustomTime = { viewModel.showCustomTimeDialog() },
                        onBack = { navController.popBackStack() },
                        onShowHistory = { viewModel.showHistoryDialog() }
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    if (useProximitySensor) {
                        ProximityVisualization(
                            currentValue = proximityValue,
                            maxValue = maxProximityValue,
                            isActive = isRunning,
                            maxRange = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)?.maximumRange ?: 5f
                        )
                    }

                    if (useMotionSensors) {
                        MotionVisualization(
                            currentValue = currentMotionValue,
                            maxValue = maxMotionValue,
                            isActive = isRunning
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimerDisplay(minutes, seconds)

                if (useProximitySensor) {
                    ProximityVisualization(
                        currentValue = proximityValue,
                        maxValue = maxProximityValue,
                        isActive = isRunning,
                        maxRange = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)?.maximumRange ?: 5f,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(80.dp)
                    )
                }

                if (useMotionSensors) {
                    MotionVisualization(
                        currentValue = currentMotionValue,
                        maxValue = maxMotionValue,
                        isActive = isRunning,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(80.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Proximity Sensor", fontSize = 12.sp)
                        Switch(
                            checked = useProximitySensor,
                            onCheckedChange = { viewModel.toggleProximitySensor(it) },
                            enabled = !isRunning
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Motion Sensors", fontSize = 12.sp)
                        Switch(
                            checked = useMotionSensors,
                            onCheckedChange = { viewModel.toggleMotionSensors(it) },
                            enabled = !isRunning
                        )
                    }
                }

                ControlButtons(
                    isRunning = isRunning,
                    onStartPause = { viewModel.toggleTimer() },
                    onReset = { viewModel.resetTimer() },
                    onSetCustomTime = { viewModel.showCustomTimeDialog() },
                    onBack = { navController.popBackStack() },
                    onShowHistory = { viewModel.showHistoryDialog() }
                )
            }
        }
    }

    if (showCustomTimeDialog) {
        CustomTimeDialog(
            onDismiss = { viewModel.dismissCustomTimeDialog() },
            onConfirm = { customMinutes, customSeconds ->
                viewModel.setCustomTime(customMinutes, customSeconds)
            }
        )
    }

    if (showHistoryDialog) {
        HistoryDialog(
            completedSessions = completedSessions,
            onDismiss = { viewModel.dismissHistoryDialog() }
        )
    }

    if (showMovementWarning) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissMovementWarning() },
            title = { Text("Focus Alert!") },
            text = { Text("Please don't use your phone during your study session!") },
            confirmButton = {
                Button(onClick = { viewModel.dismissMovementWarning() }) {
                    Text("OK")
                }
            }
        )
    }
}

@Preview(showBackground = true, name = "Pomodoro Timer Portrait")
@Composable
fun PomodoroTimerScreenPortraitPreview() {
    PomodoroTimerScreen(navController = rememberNavController())
}

@Preview(showBackground = true, name = "Pomodoro Timer Landscape", widthDp = 800, heightDp = 400)
@Composable
fun PomodoroTimerScreenLandscapePreview() {
    PomodoroTimerScreen(navController = rememberNavController())
}