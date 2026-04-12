package com.example.ozmade.main.user.profile.notification

import java.util.Date

data class NotificationItem(
    val id: Int,
    val title: String,
    val body: String,
    val type: String, // "CHAT", "ORDER", "SYSTEM"
    val createdAt: String,
    val orderId: Int? = null,
    val isRead: Boolean = false
)

object NotificationStorage {
    private val notifications = mutableListOf<NotificationItem>()
    private var listeners = mutableListOf<() -> Unit>()

    fun add(item: NotificationItem) {
        notifications.add(0, item)
        listeners.forEach { it() }
    }

    fun getAll() = notifications.toList()

    fun subscribe(listener: () -> Unit) {
        listeners.add(listener)
    }

    fun unsubscribe(listener: () -> Unit) {
        listeners.remove(listener)
    }

    fun clear() {
        notifications.clear()
        listeners.forEach { it() }
    }
}
