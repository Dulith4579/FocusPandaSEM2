package com.example.focuspanda.Data

// JsonLoader.kt


import android.content.Context
import com.example.focuspanda.Data.FeatureDetailsResponse
import com.google.gson.Gson
import java.io.IOException

object JsonLoader {
    fun loadFeaturesFromJson(context: Context): FeatureDetailsResponse {
        val jsonString: String
        try {
            jsonString = context.assets.open("feature_details.json")
                .bufferedReader()
                .use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return FeatureDetailsResponse(emptyList())
        }
        return Gson().fromJson(jsonString, FeatureDetailsResponse::class.java)
    }
}