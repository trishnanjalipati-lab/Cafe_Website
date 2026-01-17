package com.example.invisibleshield

import android.os.Bundle
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SentinelDashboard(this)
        }
    }
}

@Composable
fun SentinelDashboard(context: Context) {
    var textInput by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("AI Status: Not Trained") }

    // Variables for calculating speed
    val timings = remember { mutableListOf<Long>() }
    var lastTapTime by remember { mutableStateOf(0L) }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Invisible Shield AI", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(30.dp))

        Text("Step 1: Train the AI", style = MaterialTheme.typography.titleMedium)
        Text("Type this phrase naturally:", color = Color.Gray)

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.padding(10.dp).fillMaxWidth()
        ) {
            Text(
                "The quick brown fox jumps over the dog",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }

        TextField(
            value = textInput,
            onValueChange = { newText ->
                val now = System.currentTimeMillis()
                if (lastTapTime != 0L && newText.length > textInput.length) {
                    // Only record if adding text (not deleting)
                    timings.add(now - lastTapTime)
                }
                lastTapTime = now
                textInput = newText
            },
            label = { Text("Type here...") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (timings.isNotEmpty()) {
                    val average = timings.average().toLong()

                    // SAVE TO MEMORY
                    val prefs = context.getSharedPreferences("SENTINEL_PREFS", Context.MODE_PRIVATE)
                    prefs.edit().putLong("USER_AVG_SPEED", average).apply()

                    statusMessage = "Profile Saved! Speed: ${average}ms"
                    timings.clear()
                    textInput = "" // Clear box
                }
            },
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text("Save My Behavior Profile")
        }

        Text(statusMessage, color = Color.Blue, modifier = Modifier.padding(vertical = 10.dp))

        Divider(modifier = Modifier.padding(vertical = 20.dp))

        Text("Step 2: Activate Protection", style = MaterialTheme.typography.titleMedium)

        if (!Settings.canDrawOverlays(context)) {
            Button(
                onClick = {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        android.net.Uri.parse("package:${context.packageName}")
                    )
                    context.startActivity(intent)
                },
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Text("Grant Overlay Permission")
            }
        }
        
        Button(
            onClick = {
                // Open Accessibility Settings
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Turn ON Sentinel Service")
        }
    }
}