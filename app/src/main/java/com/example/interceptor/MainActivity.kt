package com.example.interceptor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle result if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request camera permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SetupScreen(
                        hasNotificationAccess = hasNotificationAccess(),
                        onRequestNotificationAccess = {
                            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Force recomposition if notification access was granted while in settings
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    SetupScreen(
                        hasNotificationAccess = hasNotificationAccess(),
                        onRequestNotificationAccess = {
                            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                        }
                    )
                }
            }
        }
    }

    private fun hasNotificationAccess(): Boolean {
        val enabledListeners = NotificationManagerCompat.getEnabledListenerPackages(this)
        return enabledListeners.contains(packageName)
    }
}

@Composable
fun SetupScreen(hasNotificationAccess: Boolean, onRequestNotificationAccess: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Interceptor Setup", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        if (hasNotificationAccess) {
            Text("✅ Notification Access Granted")
            Spacer(modifier = Modifier.height(16.dp))
            Text("The app is ready. It will intercept FaceTime links sent to your device.")
        } else {
            Text("❌ Notification Access Missing")
            Spacer(modifier = Modifier.height(16.dp))
            Text("To seamlessly intercept incoming FaceTime links and trigger the ringing UI, this app needs permission to read your notifications.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRequestNotificationAccess) {
                Text("Grant Notification Access")
            }
        }
    }
}
