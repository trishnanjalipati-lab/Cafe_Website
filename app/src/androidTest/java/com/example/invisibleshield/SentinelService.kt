package com.example.invisibleshield

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.WindowManager
import android.graphics.PixelFormat
import android.view.Gravity
import android.graphics.Color
import android.widget.TextView
import android.content.Context
import android.view.View
import kotlin.math.abs

class SentinelService : AccessibilityService() {

    private var lastKeyTime: Long = 0
    private var riskScore = 0
    private var overlayView: View? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // 1. Load the user's trained speed (Safe Retrieval)
        val prefs = getSharedPreferences("SENTINEL_PREFS", Context.MODE_PRIVATE)
        val userAvgSpeed = prefs.getLong("USER_AVG_SPEED", 150L) // Defaults to 150ms

        // 2. Watch for Text Change Events (Typing)
        if (event.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            val currentTime = System.currentTimeMillis()

            if (lastKeyTime != 0L) {
                val flightTime = currentTime - lastKeyTime

                // 3. Bot Detection (Instant typing like a script)
                if (flightTime < 15) {
                    triggerBlock("Bot/Automation Detected!")
                }

                // 4. Identity Check (Typing Rhythm)
                val deviation = abs(flightTime - userAvgSpeed)
                if (deviation > (userAvgSpeed * 0.7)) { // If speed varies by > 70%
                    riskScore += 20
                } else {
                    riskScore = (riskScore - 10).coerceAtLeast(0)
                }
            }

            lastKeyTime = currentTime

            // 5. Trigger Block if Risk is High
            if (riskScore >= 100) {
                triggerBlock("Identity Unverified.\nAccess Denied.")
            }
        }
    }

    private fun triggerBlock(reason: String) {
        if (overlayView != null) return // Already showing

        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, // Required for Accessibility Services
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        val view = TextView(this).apply {
            text = "STOP!\n$reason\n\nPlease re-verify in Shield App."
            setBackgroundColor(Color.parseColor("#EECC0000")) // Solid Dark Red
            setTextColor(Color.WHITE)
            textSize = 28f
            gravity = Gravity.CENTER
            setPadding(40, 40, 40, 40)
        }

        try {
            wm.addView(view, params)
            overlayView = view
        } catch (e: Exception) {
            android.util.Log.e("SENTINEL", "Overlay Error: ${e.message}")
        }
    }

    override fun onInterrupt() {}
}