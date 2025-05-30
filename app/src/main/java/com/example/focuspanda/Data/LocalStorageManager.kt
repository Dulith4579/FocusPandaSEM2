package com.example.focuspanda.Data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class LocalStorageManager(private val context: Context) {
    companion object {
        val PROFILE_IMAGE_URI = stringPreferencesKey("profile_image_uri")
    }

    suspend fun saveImageUri(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_IMAGE_URI] = uri
        }
    }

    val imageUri: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[PROFILE_IMAGE_URI]
        }
}