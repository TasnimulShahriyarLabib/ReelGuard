package com.reelguard.app.model

import kotlinx.serialization.Serializable

@Serializable
data class PlatformRule(
    val platformKey: String,
    val packageName: String,
    val triggerViewIds: List<String>,
    val homeTabIntentAction: String = "android.intent.action.MAIN"
)

@Serializable
data class ReelConfig(
    val platforms: List<PlatformRule>
)
