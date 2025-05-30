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
import kotlinx.coroutines.flow.collectLatest

import java.util.*


import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun UserProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val localStorage = remember { LocalStorageManager(context) }

    // Image state
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    // User data state
    var (initialUsername, initialEmail, initialPhone) = remember {
        mutableStateOf(Triple("", "", ""))
    }.let { state ->
        Triple(state.value.first, state.value.second, state.value.third)
    }

    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var isEmailValid by rememberSaveable { mutableStateOf(true) }
    var isPhoneValid by rememberSaveable { mutableStateOf(true) }

    // Load initial data
    LaunchedEffect(Unit) {
        localStorage.userDetails.collect { (loadedUsername, loadedEmail, loadedPhone) ->
            loadedUsername?.let {
                username = it
                initialUsername = it
            }
            loadedEmail?.let {
                email = it
                initialEmail = it
            }
            loadedPhone?.let {
                phone = it
                initialPhone = it
            }
        }

        localStorage.imageUri.collect { uriString ->
            uriString?.let { uri ->
                try {
                    val parsedUri = Uri.parse(uri)
                    if (context.contentResolver.openInputStream(parsedUri) != null) {
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

    // Validation functions
    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePhone(phone: String): Boolean {
        return phone.length == 10 && phone.all { it.isDigit() }
    }

    // Save handler
    val onSave = {
        isEmailValid = validateEmail(email)
        isPhoneValid = validatePhone(phone)

        if (isEmailValid && isPhoneValid) {
            scope.launch {
                localStorage.saveUserDetails(username, email, phone)
                isEditing = false
                Toast.makeText(context, "Changes saved", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Please fix validation errors", Toast.LENGTH_SHORT).show()
        }
    }

    // Delete handler
    val onDeleteConfirmed = {
        scope.launch {
            localStorage.deleteProfileImage()
            imageUri = null
            Toast.makeText(context, "Profile image deleted", Toast.LENGTH_SHORT).show()
        }
        showDeleteConfirmDialog = false
    }

    // Camera permission
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    // Storage permission
    val storagePermissionState = rememberPermissionState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    )

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            context.contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            scope.launch {
                localStorage.saveImageUri(it.toString())
                imageUri = it
            }
        }
    }

    // Camera functions
    fun createTempImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", ".jpg", storageDir
        ).apply { createNewFile() }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri?.let { uri ->
                scope.launch {
                    try {
                        context.contentResolver.openInputStream(uri)?.use {
                            localStorage.saveProfileImage(uri)
                        }
                        localStorage.imageUri.collectLatest { uriString ->
                            imageUri = uriString?.let { Uri.parse(it) }
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    fun takePhoto() {
        try {
            val photoFile = createTempImageFile()
            val photoUri = FileProvider.getUriForFile(
                context, "${context.packageName}.fileprovider", photoFile
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

    fun selectFromGallery() {
        if (storagePermissionState.status.isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            storagePermissionState.launchPermissionRequest()
        }
    }

    val onImageSourceSelected: (ImageSource) -> Unit = { source ->
        when (source) {
            ImageSource.CAMERA -> takePhoto()
            ImageSource.GALLERY -> selectFromGallery()
        }
        showImageSourceDialog = false
    }

    // UI
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("User Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
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
                    ProfileImageSection(
                        imageUri = imageUri,
                        onImageClick = { showImageSourceDialog = true }
                    )
                    ProfileDetailsSection(
                        username = username,
                        onUsernameChange = { username = it },
                        email = email,
                        onEmailChange = { email = it },
                        phone = phone,
                        onPhoneChange = { phone = it },
                        isEditing = isEditing,
                        isEmailValid = isEmailValid,
                        isPhoneValid = isPhoneValid,
                        onEditClick = { isEditing = true },
                        onSaveClick = {onSave()},
                        onCancelClick = {
                            username = initialUsername
                            email = initialEmail
                            phone = initialPhone
                            isEditing = false
                        },
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
                    ProfileImageSection(
                        imageUri = imageUri,
                        onImageClick = { showImageSourceDialog = true }
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    ProfileDetailsSection(
                        username = username,
                        onUsernameChange = { username = it },
                        email = email,
                        onEmailChange = { email = it },
                        phone = phone,
                        onPhoneChange = { phone = it },
                        isEditing = isEditing,
                        isEmailValid = isEmailValid,
                        isPhoneValid = isPhoneValid,
                        onEditClick = { isEditing = true },
                        onSaveClick = {onSave()},
                        onCancelClick = {
                            username = initialUsername
                            email = initialEmail
                            phone = initialPhone
                            isEditing = false
                        },
                        onDeleteClick = { showDeleteConfirmDialog = true }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showImageSourceDialog) {
        ImageSourceDialog(
            onSourceSelected = onImageSourceSelected,
            onDismiss = { showImageSourceDialog = false }
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
            .clickable(onClick = onImageClick)
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
private fun ProfileDetailsSection(
    username: String,
    onUsernameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    isEditing: Boolean,
    isEmailValid: Boolean,
    isPhoneValid: Boolean,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isEditing) {
                OutlinedTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isEmailValid,
                    supportingText = {
                        if (!isEmailValid) {
                            Text("Please enter a valid email")
                        }
                    }
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = onPhoneChange,
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isPhoneValid,
                    supportingText = {
                        if (!isPhoneValid) {
                            Text("Phone must be 10 digits")
                        }
                    }
                )
            } else {
                ProfileDetailRow("Username", username)
                ProfileDetailRow("Email", email)
                ProfileDetailRow("Phone", phone)
                ProfileDetailRow("Password", "••••••••")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (isEditing) {
                    Button(onClick = onSaveClick) {
                        Text("Save")
                    }
                    Button(
                        onClick = onCancelClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text("Cancel")
                    }
                } else {
                    Button(onClick = onEditClick) {
                        Text("Edit")
                    }
                    Button(
                        onClick = onDeleteClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete Image")
                    }
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
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ImageSourceDialog(
    onSourceSelected: (ImageSource) -> Unit,
    onDismiss: () -> Unit
) {
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
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Delete") },
        text = { Text("Are you sure you want to delete your profile image?") },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
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
        if (!exists()) mkdirs()
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