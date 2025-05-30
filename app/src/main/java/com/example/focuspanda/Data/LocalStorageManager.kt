package com.example.focuspanda.Data

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.File





val Context.dataStore by preferencesDataStore(name = "user_prefs")

class LocalStorageManager(private val context: Context) {
    companion object {
        val PROFILE_IMAGE_URI = stringPreferencesKey("profile_image_uri")
        val USERNAME = stringPreferencesKey("username")
        val EMAIL = stringPreferencesKey("email")
        val PHONE = stringPreferencesKey("phone")
    }

    suspend fun saveImageUri(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_IMAGE_URI] = uri
        }
    }

    suspend fun saveUserDetails(username: String, email: String, phone: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME] = username
            preferences[EMAIL] = email
            preferences[PHONE] = phone
        }
    }

    suspend fun saveProfileImage(uri: Uri) {
        try {
            // Create profile images directory if it doesn't exist
            val profileDir = File(context.filesDir, "profile_images").apply {
                if (!exists()) mkdirs()
            }

            // Create destination file
            val destFile = File(profileDir, "profile_picture_${System.currentTimeMillis()}.jpg")

            // Copy the content from URI to destination file
            context.contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Save the URI of the new file
            saveImageUri(destFile.toUri().toString())
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to saving the original URI
            saveImageUri(uri.toString())
        }
    }

    suspend fun deleteProfileImage() {
        try {
            // Get the current URI string from preferences
            val uriString = context.dataStore.data
                .map { it[PROFILE_IMAGE_URI] }
                .first()

            uriString?.let { uri ->
                try {
                    // Parse the URI
                    val parsedUri = Uri.parse(uri)

                    // Handle file URIs (for images we've saved to our directory)
                    if (parsedUri.scheme == "file") {
                        val filePath = parsedUri.path ?: return@let
                        val file = File(filePath)
                        if (file.exists()) {
                            file.delete()
                        }
                    }
                    // For content URIs (like from gallery), we can't delete the original
                    // but we can remove our reference to it
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Clear the stored URI regardless of whether we could delete the file
                saveImageUri("")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    val imageUri: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PROFILE_IMAGE_URI]?.takeIf { it.isNotEmpty() }?.let { uriString ->
                try {
                    val uri = Uri.parse(uriString)
                    // Verify the file exists
                    if (uri.scheme == "file") {
                        if (!File(uri.path ?: "").exists()) return@let null
                    }
                    uriString
                } catch (e: Exception) {
                    null
                }
            }
        }

    val userDetails: Flow<Triple<String?, String?, String?>> = context.dataStore.data
        .map { preferences ->
            Triple(
                preferences[USERNAME],
                preferences[EMAIL],
                preferences[PHONE]
            )
        }
}