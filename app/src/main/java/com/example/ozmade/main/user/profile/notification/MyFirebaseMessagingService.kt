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
import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.FCMTokenRequest
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var api: OzMadeApi

    companion object {
        private const val CHANNEL_CHAT_ID = "ozmade_chat_messages"
        private const val CHANNEL_ORDERS_ID = "ozmade_orders"
        private const val CHANNEL_NAME_CHAT = "Чат и сообщения"
        private const val CHANNEL_NAME_ORDERS = "Заказы и коды"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data
        Log.d("FCM", "Message received. Data: $data")

        val type = data["type"] ?: "CHAT"
        val title = remoteMessage.notification?.title ?: data["title"] ?: "OzMade"
        val body = remoteMessage.notification?.body ?: data["body"] ?: ""

        // Сохраняем в локальную историю
        NotificationStorage.add(
            NotificationItem(
                id = UUID.randomUUID().toString(),
                title = title,
                body = body,
                type = type,
                orderId = data["order_id"]?.toIntOrNull()
            )
        )

        // Показываем системное уведомление
        if (type == "ORDER" || data.containsKey("order_id")) {
            val orderId = data["order_id"]?.toIntOrNull() ?: 0
            showOrderNotification(title, body, orderId, data)
        } else {
            val chatId = data["chat_id"]?.toIntOrNull()
            showChatNotification(title, body, chatId, data)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "New token generated: $token")
        // Отправляем новый токен на сервер, чтобы не потерять связь
        CoroutineScope(Dispatchers.IO).launch {
            try {
                api.updateFCMToken(FCMTokenRequest(token))
            } catch (e: Exception) {
                Log.e("FCM", "Failed to update token on server", e)
            }
        }
    }

    private fun showOrderNotification(title: String, body: String, orderId: Int, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("open_order_history", true)
            putExtra("notification_title", title)
            putExtra("notification_body", body)
            putExtra("notification_type", "ORDER")
            // Прокидываем все данные из push
            data.forEach { (k, v) -> putExtra(k, v) }
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            (orderId + System.currentTimeMillis().toInt()),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ORDERS_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .build()

        notifySafely(orderId + 1000, notification)
    }

    private fun showChatNotification(title: String, body: String, chatId: Int?, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("open_chat", true)
            putExtra("notification_title", title)
            putExtra("notification_body", body)
            putExtra("notification_type", "CHAT")
            data.forEach { (k, v) -> putExtra(k, v) }
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            (chatId ?: System.currentTimeMillis().toInt()),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_CHAT_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .build()

        notifySafely(chatId ?: 1, notification)
    }

    private fun notifySafely(id: Int, notification: android.app.Notification) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
            }
            NotificationManagerCompat.from(this).notify(id, notification)
        } catch (e: Exception) {
            Log.e("FCM", "Error showing notification", e)
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            
            val chatChannel = NotificationChannel(CHANNEL_CHAT_ID, CHANNEL_NAME_CHAT, NotificationManager.IMPORTANCE_HIGH).apply {
                enableLights(true)
                enableVibration(true)
            }
            
            val orderChannel = NotificationChannel(CHANNEL_ORDERS_ID, CHANNEL_NAME_ORDERS, NotificationManager.IMPORTANCE_HIGH).apply {
                enableLights(true)
                enableVibration(true)
            }

            manager.createNotificationChannel(chatChannel)
            manager.createNotificationChannel(orderChannel)
        }
    }
}
