package com.example.interceptor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

class CallRingingActivity : ComponentActivity() {

    private var faceTimeUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Make activity show over lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        faceTimeUrl = intent.getStringExtra("FACETIME_URL")

        setContent {
            MaterialTheme {
                RingingScreen(
                    onAccept = {
                        faceTimeUrl?.let { url ->
                            val builder = CustomTabsIntent.Builder()
                            val customTabsIntent = builder.build()
                            customTabsIntent.launchUrl(this, Uri.parse(url))
                        }
                        finish()
                    },
                    onDecline = {
                        finish()
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle new intent if activity is already on top
        intent.getStringExtra("FACETIME_URL")?.let {
            faceTimeUrl = it
        }
    }
}

@Composable
fun RingingScreen(onAccept: () -> Unit, onDecline: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        
        val hasCameraPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                        
                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview
                            )
                        } catch(e: Exception) {
                            // Ignore error
                        }
                    }, ContextCompat.getMainExecutor(ctx))
                    
                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))
        }

        // Overlay UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(48.dp))
                Text("FaceTime", style = MaterialTheme.typography.headlineLarge, color = Color.White)
                Text("Incoming Call...", style = MaterialTheme.typography.titleMedium, color = Color.White)
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FloatingActionButton(
                    onClick = onDecline,
                    containerColor = Color.Red,
                    shape = CircleShape,
                    modifier = Modifier.size(80.dp)
                ) {
                    Text("Decline", color = Color.White)
                }
                
                FloatingActionButton(
                    onClick = onAccept,
                    containerColor = Color(0xFF4CAF50), // Green
                    shape = CircleShape,
                    modifier = Modifier.size(80.dp)
                ) {
                    Text("Accept", color = Color.White)
                }
            }
        }
    }
}
