package com.reelguard.app.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.reelguard.app.model.AppConfig
import com.reelguard.app.util.ConfigLoader

class ReelBlockerService : AccessibilityService() {

    private lateinit var appConfig: AppConfig

    override fun onServiceConnected() {
        super.onServiceConnected()
        appConfig = ConfigLoader.loadConfig(this)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            
            val packageName = event.packageName?.toString() ?: return
            val rootNode = rootInActiveWindow ?: return

            val matchingPlatform = appConfig.platforms.find { it.packageName == packageName }
            if (matchingPlatform != null) {
                for (viewId in matchingPlatform.triggerViewIds) {
                    if (containsNodeWithId(rootNode, viewId)) {
                        performGlobalAction(GLOBAL_ACTION_HOME)
                        break
                    }
                }
            }
            rootNode.recycle()
        }
    }

    private fun containsNodeWithId(node: AccessibilityNodeInfo, targetId: String): Boolean {
        if (node.viewIdResourceName == targetId) {
            return true
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            if (containsNodeWithId(child, targetId)) {
                child.recycle()
                return true
            }
            child.recycle()
        }
        return false
    }

    override fun onInterrupt() {}
}
