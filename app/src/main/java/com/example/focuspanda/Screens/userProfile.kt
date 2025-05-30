package com.example.focuspanda.Screens



import android.Manifest
import android.Manifest.permission.CAMERA
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.focuspanda.Data.LocalStorageManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.ContentResolverCompat



import kotlinx.coroutines.launch



import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.compose.runtime.saveable.rememberSaveable

import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun UserProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val localStorage = remember { LocalStorageManager(context) }

    // Use rememberSaveable to maintain state across configuration changes
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    var showImageSourceDialog by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // Camera permission
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    // Storage permission (handles different API levels)
    val storagePermissionState = rememberPermissionState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )

    // Camera launcher - handles the photo capture result
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri?.let { uri ->
                scope.launch {
                    localStorage.saveImageUri(uri.toString())
                    // Force refresh the image
                    imageUri = uri
                }
            }
        }
    }

    // Gallery launcher - handles image selection
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Create a persistent URI permission
            context.contentResolver.takePersistableUriPermission(
                it,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            scope.launch {
                localStorage.saveImageUri(it.toString())
                imageUri = it
            }
        }
    }

    // Load saved image on first composition
    LaunchedEffect(Unit) {
        localStorage.imageUri.collect { uriString ->
            uriString?.takeIf { it.isNotEmpty() }?.let { uri ->
                try {
                    val parsedUri = Uri.parse(uri)
                    // Check if URI is still accessible
                    val exists = try {
                        context.contentResolver.openInputStream(parsedUri) != null
                    } catch (e: Exception) {
                        false
                    }
                    if (exists) {
                        imageUri = parsedUri
                    } else {
                        scope.launch { localStorage.saveImageUri("") }
                    }
                } catch (e: Exception) {
                    scope.launch { localStorage.saveImageUri("") }
                }
            }
        }
    }

    // Create temp file for camera capture
    fun createTempImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            createNewFile()
        }
    }

    // Take photo function
    fun takePhoto() {
        try {
            val photoFile = createTempImageFile()
            val photoUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                photoFile
            )
            imageUri = photoUri

            if (cameraPermissionState.status.isGranted) {
                cameraLauncher.launch(photoUri)
            } else {
                cameraPermissionState.launchPermissionRequest()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to create image file", Toast.LENGTH_SHORT).show()
        }
    }

    // Select from gallery function
    fun selectFromGallery() {
        if (storagePermissionState.status.isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            storagePermissionState.launchPermissionRequest()
        }
    }

    // Handle image source selection
    val onImageSourceSelected: (ImageSource) -> Unit = { source ->
        when (source) {
            ImageSource.CAMERA -> takePhoto()
            ImageSource.GALLERY -> selectFromGallery()
        }
        showImageSourceDialog = false
    }

    // Handle delete confirmation
    val onDeleteConfirmed = {
        scope.launch {
            localStorage.saveImageUri("")
            imageUri = null
        }
        showDeleteConfirmDialog = false
    }

    // Rest of your UI code remains the same...
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("User Profile", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        // ... your existing UI code ...

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            val isLandscape = maxWidth > maxHeight

            if (isLandscape) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileImageSection(imageUri = imageUri, onImageClick = { showImageSourceDialog = true })
                    ProfileDetailsSection(
                        onEditClick = { /* Edit logic */ },
                        onDeleteClick = { showDeleteConfirmDialog = true }
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ProfileImageSection(imageUri = imageUri, onImageClick = { showImageSourceDialog = true })
                    Spacer(modifier = Modifier.height(32.dp))
                    ProfileDetailsSection(
                        onEditClick = { /* Edit logic */ },
                        onDeleteClick = { showDeleteConfirmDialog = true }
                    )
                }
            }
        }
    }

    if (showImageSourceDialog) {
        ImageSourceDialog(
            onSourceSelected = onImageSourceSelected,
            onDismiss = { showImageSourceDialog = false }
        )
    }


    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text("Permission Required") },
            text = { Text("This permission is needed to select profile pictures.") },
            confirmButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (showDeleteConfirmDialog) {
        DeleteConfirmationDialog(
            onConfirm = onDeleteConfirmed,
            onDismiss = { showDeleteConfirmDialog = false }
        )
    }
}


@Composable
private fun ProfileImageSection(imageUri: Uri?, onImageClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(CircleShape)
            .clickable { onImageClick() }
            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
    ) {
        val painter = if (imageUri != null) {
            rememberAsyncImagePainter(
                ImageRequest.Builder(LocalContext.current)
                    .data(imageUri)
                    .error(R.drawable.dilshan)
                    .build()
            )
        } else {
            painterResource(id = R.drawable.dilshan)
        }

        Image(
            painter = painter,
            contentDescription = "Profile Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            // Image captured successfully
            // Do something with imageUri
        }
    }

    Button(onClick = {
        val photoFile = createImageFile(context)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            photoFile
        )
        imageUri = uri
        cameraLauncher.launch(uri)
    }) {
        Text("Take Photo")
    }

    imageUri?.let {
        Image(
            painter = rememberAsyncImagePainter(it),
            contentDescription = "Profile Image",
            modifier = Modifier.size(120.dp).clip(CircleShape)
        )
    }
}


@Composable
private fun ProfileDetailsSection(onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileDetailRow("Username", "Dilshan")
            ProfileDetailRow("Email", "jjk@gmail.com")
            ProfileDetailRow("Phone", "0712345676")
            ProfileDetailRow("Password", "••••••••")

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = onEditClick) { Text("Edit") }
                Button(onClick = onDeleteClick, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun ImageSourceDialog(onSourceSelected: (ImageSource) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Image Source") },
        text = { Text("Choose how to select your profile image") },
        confirmButton = {
            Button(onClick = { onSourceSelected(ImageSource.CAMERA) }) {
                Text("Camera")
            }
        },
        dismissButton = {
            Button(onClick = { onSourceSelected(ImageSource.GALLERY) }) {
                Text("Gallery")
            }
        }
    )
}

@Composable
private fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Delete") },
        text = { Text("Are you sure you want to delete your profile image?") },
        confirmButton = {
            TextButton(onClick = onConfirm, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private enum class ImageSource {
    CAMERA, GALLERY
}
private fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = File(context.getExternalFilesDir(null), "images").apply {
        if (!exists()) mkdirs() // create directory if not exists
    }
    return File(storageDir, "JPEG_${timeStamp}.jpg")
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun UserProfileScreenPreview() {
    MaterialTheme {
        UserProfileScreen(navController = rememberNavController())
    }
}