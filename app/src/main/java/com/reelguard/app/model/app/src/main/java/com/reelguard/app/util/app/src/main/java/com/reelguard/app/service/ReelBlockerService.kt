package com.reelguard.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.reelguard.app.model.PlatformRule
import com.reelguard.app.util.ConfigLoader

class ReelBlockerService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private var rulesMap = mapOf<String, PlatformRule>()
    private var lastBlockedTime = 0L

    override fun onServiceConnected() {
        super.onServiceConnected()
        loadRules()
        Log.d("ReelBlockerService", "Multi-platform Service Connected.")
    }

    private fun loadRules() {
        val config = ConfigLoader.loadConfig(this)
        rulesMap = config.platforms.associateBy { it.packageName }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return
        val packageName = event.packageName?.toString() ?: return
        val rule = rulesMap[packageName] ?: return

        val rootNode = rootInActiveWindow ?: return
        if (isReelDetected(rootNode, rule)) {
            triggerBlockAction(rule)
        }
        rootNode.recycle()
    }

    private fun isReelDetected(node: AccessibilityNodeInfo, rule: PlatformRule): Boolean {
        for (viewId in rule.triggerViewIds) {
            val matchedNodes = node.findAccessibilityNodeInfosByViewId(viewId)
            if (!matchedNodes.isNullOrEmpty()) {
                matchedNodes.forEach { it.recycle() }
                return true
            }
        }
        return false
    }

    private fun triggerBlockAction(rule: PlatformRule) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBlockedTime < 1500) return
        lastBlockedTime = currentTime

        Toast.makeText(this, "${rule.platformKey.replace("_", " ")} Blocked!", Toast.LENGTH_SHORT).show()
        performGlobalAction(GLOBAL_ACTION_BACK)

        handler.postDelayed({
            val currentRoot = rootInActiveWindow
            if (currentRoot != null && isReelDetected(currentRoot, rule)) {
                val launchIntent = packageManager.getLaunchIntentForPackage(rule.packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(launchIntent)
                }
                currentRoot.recycle()
            }
        }, 500)
    }

    override fun onInterrupt() {}
}
