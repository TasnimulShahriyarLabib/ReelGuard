package com.reelguard.app.util

import android.content.Context
import com.reelguard.app.model.AppConfig
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader

object ConfigLoader {
    fun loadConfig(context: Context): AppConfig {
        returntry {
            val inputStream = context.assets.open("reel_config.json")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val jsonString = reader.readText()
            reader.close()
            Json { ignoreUnknownKeys = true }.decodeFromString(AppConfig.serializer(), jsonString)
        } catch (e: Exception) {
            AppConfig(emptyList())
        }
    }
}
