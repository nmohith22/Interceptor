package com.example.interceptor

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class IncomingLinkNotificationService : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        sbn?.notification?.extras?.let { extras ->
            val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: return
            
            Log.d("Interceptor", "Received notification text: $text")

            val urlRegex = "https://facetime\\.apple\\.com/\\S+".toRegex()
            val match = urlRegex.find(text)

            if (match != null) {
                val url = match.value
                Log.d("Interceptor", "Found FaceTime link: $url")
                
                // Trigger the ringing UI
                val intent = Intent(this, CallRingingActivity::class.java).apply {
                    putExtra("FACETIME_URL", url)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }
                startActivity(intent)
            }
        }
    }
}
