package com.reelguard.app.util

import android.content.Context
import com.reelguard.app.model.ReelConfig
import kotlinx.serialization.json.Json

object ConfigLoader {
    private val json = Json { ignoreUnknownKeys = true }

    fun loadConfig(context: Context): ReelConfig {
        return try {
            val jsonString = context.assets.open("reel_config.json").bufferedReader().use { it.readText() }
            json.decodeFromString(ReelConfig.serializer(), jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            ReelConfig(emptyList())
        }
    }
}
