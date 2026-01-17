package com.example.invisibleshield

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import kotlin.math.abs

class SentinelService : AccessibilityService() {

    private var userAvgSpeed: Long = 0
    private var lastEventTime: Long = 0
    private var windowManager: WindowManager? = null
    private var shieldView: TextView? = null
    private var isShieldActive = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.d("SentinelService", "Service Connected")

        // Load Training Data
        val prefs = getSharedPreferences("SENTINEL_PREFS", Context.MODE_PRIVATE)
        userAvgSpeed = prefs.getLong("USER_AVG_SPEED", 0)
        Log.d("SentinelService", "Loaded User Speed: $userAvgSpeed ms")

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // We only care about text changes
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            val now = System.currentTimeMillis()
            if (lastEventTime != 0L) {
                val speed = now - lastEventTime
                Log.d("SentinelService", "Keystroke Speed: $speed ms")

                if (userAvgSpeed > 0) {
                    analyzeBehavior(speed)
                }
            }
            lastEventTime = now
        }
    }

    private fun analyzeBehavior(currentSpeed: Long) {
        // Simple Anomaly Detection: +/- 50% variance allowed
        // If user avg is 100ms, allowed range is 50ms - 150ms
        val variance = userAvgSpeed * 0.5
        val minSpeed = userAvgSpeed - variance
        val maxSpeed = userAvgSpeed + variance

        if (currentSpeed < minSpeed || currentSpeed > maxSpeed) {
            Log.w("SentinelService", "ANOMALY DETECTED! Speed: $currentSpeed (Range: $minSpeed-$maxSpeed)")
            triggerDefenseProtocol()
        }
    }

    private fun triggerDefenseProtocol() {
        if (isShieldActive) return

        isShieldActive = true
        handler.post {
            showShield()
        }
    }

    private fun showShield() {
        if (shieldView == null) {
            shieldView = TextView(this).apply {
                text = "SECURITY BLOCK!\nUnusual Behavior Detected"
                textSize = 30f
                setTextColor(Color.WHITE)
                setBackgroundColor(Color.RED)
                gravity = Gravity.CENTER
            }
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )

        try {
            windowManager?.addView(shieldView, params)
            
            // Auto-dismiss for testing purposes
            handler.postDelayed({
                removeShield()
            }, 5000) // 5 seconds lock
        } catch (e: Exception) {
            Log.e("SentinelService", "Error showing shield: ${e.message}")
        }
    }

    private fun removeShield() {
        try {
            if (shieldView != null && isShieldActive) {
                windowManager?.removeView(shieldView)
                isShieldActive = false
            }
        } catch (e: Exception) {
            Log.e("SentinelService", "Error removing shield: ${e.message}")
        }
    }

    override fun onInterrupt() {
        Log.d("SentinelService", "Service Interrupted")
    }
}
