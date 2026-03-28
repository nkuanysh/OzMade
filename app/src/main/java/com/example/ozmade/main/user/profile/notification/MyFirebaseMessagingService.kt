package com.example.ozmade.main.user.profile.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.ozmade.MainActivity
import com.example.ozmade.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val CHANNEL_ID = "ozmade_chat_messages"
        private const val CHANNEL_NAME = "Chat messages"
        private const val CHANNEL_DESCRIPTION = "Notifications about new chat messages"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCM", "Message data = ${remoteMessage.data}")
        Log.d("FCM", "Notification title = ${remoteMessage.notification?.title}")
        Log.d("FCM", "Notification body = ${remoteMessage.notification?.body}")

        val data = remoteMessage.data

        val chatId = data["chat_id"]?.toIntOrNull()
        val sellerId = data["seller_id"]?.toIntOrNull() ?: 0
        val productId = data["product_id"]?.toIntOrNull() ?: 0
        val sellerName = data["seller_name"] ?: "Продавец"
        val productTitle = data["product_title"] ?: "Товар"
        val price = data["price"]?.toIntOrNull() ?: 0

        val title = remoteMessage.notification?.title
            ?: data["title"]
            ?: "Новое сообщение"

        val body = remoteMessage.notification?.body
            ?: data["body"]
            ?: "У вас новое сообщение в чате"

        showChatNotification(
            title = title,
            body = body,
            chatId = chatId,
            sellerId = sellerId,
            productId = productId,
            sellerName = sellerName,
            productTitle = productTitle,
            price = price
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token: $token")
    }

    private fun showChatNotification(
        title: String,
        body: String,
        chatId: Int?,
        sellerId: Int,
        productId: Int,
        sellerName: String,
        productTitle: String,
        price: Int
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("open_chat", true)
            putExtra("chat_id", chatId ?: 0)
            putExtra("seller_id", sellerId)
            putExtra("product_id", productId)
            putExtra("seller_name", sellerName)
            putExtra("product_title", productTitle)
            putExtra("price", price)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            chatId ?: System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                Log.w("FCM", "POST_NOTIFICATIONS permission not granted")
                return
            }
        }

        NotificationManagerCompat.from(this).notify(chatId ?: 1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}