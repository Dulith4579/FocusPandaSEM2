package com.example.app.settings

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.os.Build
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import java.time.LocalTime
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.focuspanda.Model.BatteryMonitor
import com.example.focuspanda.Model.BatteryStatus
import com.example.focuspanda.Model.NetworkMonitor
import kotlinx.coroutines.delay
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val systemDarkMode = isSystemInDarkTheme()

    // Theme control
    var darkModeSetting by remember { mutableStateOf("system") }
    var manualDarkMode by remember { mutableStateOf(false) }

    // Light Sensor
    val lightSensorAvailable = remember {
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT)
    }
    var isAmbientLightEnabled by remember { mutableStateOf(lightSensorAvailable) }
    var currentLightLevel by remember { mutableStateOf(0f) }
    var lightThreshold by remember { mutableStateOf(50f) }

    // Network Status
    val connectivityManager = remember {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    val isNetworkConnected by NetworkMonitor.networkState(connectivityManager)
        .collectAsState(initial = false)

    // Battery Status
    val batteryStatus by BatteryMonitor.batteryState(context)
        .collectAsState(initial = BatteryStatus())

    // Brightness control
    var brightness by remember { mutableFloatStateOf(getCurrentBrightness(context)) }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(
                context,
                "Permission denied - can't change brightness",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Network status message
    var showNetworkMessage by remember { mutableStateOf(false) }
    var networkMessage by remember { mutableStateOf("") }
    var networkMessageColor by remember { mutableStateOf(Color.Green) }

    // Determine current dark mode state
    val isDarkModeEnabled = when (darkModeSetting) {
        "manual" -> manualDarkMode
        "time" -> {
            val hour = LocalTime.now().hour
            hour >= 18 || hour < 6
        }
        "sensor" -> currentLightLevel < lightThreshold
        "battery" -> batteryStatus.isPowerSaveMode || batteryStatus.level < 20
        "system" -> systemDarkMode
        else -> systemDarkMode
    }

    // Apply theme - using dynamicColor if available
    val colorScheme = if (isDarkModeEnabled) {
        dynamicDarkColorScheme(context)
    } else {
        dynamicLightColorScheme(context)
    }

    // Network status observer
    LaunchedEffect(isNetworkConnected) {
        if (!isNetworkConnected) {
            networkMessage = "No internet connection"
            networkMessageColor = Color.Red
            showNetworkMessage = true
        } else {
            networkMessage = "Back online"
            networkMessageColor = Color.Green
            showNetworkMessage = true
            delay(3000)
            showNetworkMessage = false
        }
    }

    // Register light sensor if available
    if (lightSensorAvailable && isAmbientLightEnabled) {
        DisposableEffect(Unit) {
            val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    currentLightLevel = event.values[0]
                }
                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
            }

            sensorManager.registerListener(
                listener,
                lightSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )

            onDispose {
                sensorManager.unregisterListener(listener)
            }
        }
    }

    MaterialTheme(colorScheme = colorScheme) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Device Settings",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Dark Mode Settings
                    DarkModeSettingsCard(
                        darkModeSetting = darkModeSetting,
                        isEnabled = manualDarkMode,
                        isDarkMode = isDarkModeEnabled,
                        onModeChange = { darkModeSetting = it },
                        onToggle = { manualDarkMode = it },
                        adaptiveColor = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Brightness Control
                    BrightnessControlCard(
                        brightness = brightness,
                        onBrightnessChange = { newValue ->
                            brightness = newValue
                            if (Settings.System.canWrite(context)) {
                                setBrightness(context, brightness)
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    requestPermissionLauncher.launch(Manifest.permission.WRITE_SETTINGS)
                                }
                            }
                        },
                        adaptiveColor = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Ambient Light Sensor (if available)
                    if (lightSensorAvailable) {
                        AmbientLightCard(
                            isEnabled = isAmbientLightEnabled,
                            currentLevel = currentLightLevel,
                            threshold = lightThreshold,
                            onToggle = { isAmbientLightEnabled = it },
                            onThresholdChange = { lightThreshold = it },
                            adaptiveColor = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Battery Status
                    BatteryStatusCard(
                        level = batteryStatus.level,
                        isSaver = batteryStatus.isPowerSaveMode,
                        adaptiveColor = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Network Status
                    NetworkStatusCard(
                        isConnected = isNetworkConnected,
                        adaptiveColor = MaterialTheme.colorScheme.primary
                    )
                }

                // Network status snackbar
                AnimatedVisibility(
                    visible = showNetworkMessage,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Surface(
                        color = networkMessageColor,
                        shape = MaterialTheme.shapes.medium,
                        shadowElevation = 8.dp,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(
                            text = networkMessage,
                            color = Color.White,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

private fun getCurrentBrightness(context: Context): Float {
    return try {
        Settings.System.getInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS
        ) / 255f
    } catch (e: Settings.SettingNotFoundException) {
        0.5f
    }
}

private fun setBrightness(context: Context, brightness: Float) {
    try {
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            (brightness * 255).toInt()
        )
    } catch (e: SecurityException) {
        Toast.makeText(
            context,
            "Please grant WRITE_SETTINGS permission",
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Composable
private fun BrightnessControlCard(
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    adaptiveColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = adaptiveColor.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Screen Brightness",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = adaptiveColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = brightness,
                onValueChange = onBrightnessChange,
                valueRange = 0f..1f,
                steps = 10,
                colors = SliderDefaults.colors(
                    thumbColor = adaptiveColor,
                    activeTrackColor = adaptiveColor.copy(alpha = 0.5f)
                )
            )
            Text(
                "Brightness: ${(brightness * 100).toInt()}%",
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
internal fun DarkModeSettingsCard(
    darkModeSetting: String,
    isEnabled: Boolean,
    isDarkMode: Boolean,
    onModeChange: (String) -> Unit,
    onToggle: (Boolean) -> Unit,
    adaptiveColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = adaptiveColor.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Dark Mode",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = adaptiveColor
            )
            Spacer(modifier = Modifier.height(8.dp))

            DarkModeOption("System Default", darkModeSetting == "system", adaptiveColor) {
                onModeChange("system")
            }
            DarkModeOption("Manual", darkModeSetting == "manual", adaptiveColor) {
                onModeChange("manual")
            }
            DarkModeOption("Time-based", darkModeSetting == "time", adaptiveColor) {
                onModeChange("time")
            }
            DarkModeOption("Light Sensor", darkModeSetting == "sensor", adaptiveColor) {
                onModeChange("sensor")
            }
            DarkModeOption("Battery Saver", darkModeSetting == "battery", adaptiveColor) {
                onModeChange("battery")
            }

            if (darkModeSetting == "manual") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Text("Enable Dark Mode", color = MaterialTheme.colorScheme.onSurface)
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = onToggle,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = adaptiveColor,
                            checkedTrackColor = adaptiveColor.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            Text(
                "Current Mode: ${if (isDarkMode) "Dark" else "Light"}",
                color = adaptiveColor.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
internal fun AmbientLightCard(
    isEnabled: Boolean,
    currentLevel: Float,
    threshold: Float,
    onToggle: (Boolean) -> Unit,
    onThresholdChange: (Float) -> Unit,
    adaptiveColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = adaptiveColor .copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Ambient Light Sensor",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = adaptiveColor
                )
                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = adaptiveColor,
                        checkedTrackColor = adaptiveColor.copy(alpha = 0.5f)
                    )
                )
            }

            if (isEnabled) {
                Text(
                    "Current Light Level: ${"%.1f".format(currentLevel)} lx",
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Slider(
                    value = threshold,
                    onValueChange = onThresholdChange,
                    valueRange = 10f..1000f,
                    steps = 9,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = adaptiveColor,
                        activeTrackColor = adaptiveColor.copy(alpha = 0.5f)
                    )
                )
                Text(
                    "Dark Mode Threshold: ${"%.1f".format(threshold)} lx",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
internal fun NetworkStatusCard(
    isConnected: Boolean,
    adaptiveColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = adaptiveColor.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Network Status",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = adaptiveColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Connected: ${if (isConnected) "Yes" else "No"}",
                color = if (isConnected) adaptiveColor else Color.Red
            )
            Text(
                "Connection Type: ${if (isConnected) "Active" else "Disconnected"}",
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
internal fun BatteryStatusCard(
    level: Int,
    isSaver: Boolean,
    adaptiveColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = adaptiveColor.copy(alpha = 0.1f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Battery Status",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = adaptiveColor
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Level: $level%",
                color = when {
                    level < 20 -> Color.Red
                    level < 50 -> Color.Yellow
                    else -> adaptiveColor
                }
            )

            LinearProgressIndicator(
                progress = level / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(vertical = 8.dp),
                color = adaptiveColor,
                trackColor = adaptiveColor.copy(alpha = 0.2f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Power Saver Mode",
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    if (isSaver) "ON" else "OFF",
                    color = if (isSaver) Color.Red else adaptiveColor
                )
            }
        }
    }
}

@Composable
private fun DarkModeOption(
    title: String,
    selected: Boolean,
    adaptiveGreen: Color,
    onSelected: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelected,
            colors = RadioButtonDefaults.colors(
                selectedColor = adaptiveGreen
            )
        )
        Text(
            title,
            modifier = Modifier.padding(start = 8.dp),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen(navController = rememberNavController())
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingsScreenDarkPreview() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        SettingsScreen(navController = rememberNavController())
    }
}