package com.reelguard.app.model

import kotlinx.serialization.Serializable

@Serializable
data class PlatformRule(
    val platformKey: String,
    val packageName: String,
    val triggerViewIds: List<String>
)

@Serializable
data class AppConfig(
    val platforms: List<PlatformRule>
)
