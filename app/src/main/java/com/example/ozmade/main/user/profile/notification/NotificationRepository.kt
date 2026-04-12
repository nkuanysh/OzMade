package com.example.ozmade.main.user.profile.notification

import com.example.ozmade.network.api.OzMadeApi
import com.example.ozmade.network.model.NotificationDto
import javax.inject.Inject
import javax.inject.Singleton

interface NotificationRepository {
    suspend fun getNotifications(): List<NotificationItem>
    suspend fun markAsRead(id: Int)
    suspend fun markAllAsRead()
}

@Singleton
class RealNotificationRepository @Inject constructor(
    private val api: OzMadeApi
) : NotificationRepository {

    override suspend fun getNotifications(): List<NotificationItem> {
        val response = api.getNotifications()
        if (response.isSuccessful) {
            return response.body()?.map { it.toDomain() } ?: emptyList()
        }
        throw Exception("Failed to fetch notifications")
    }

    override suspend fun markAsRead(id: Int) {
        api.markNotificationRead(id)
    }

    override suspend fun markAllAsRead() {
        api.markAllNotificationsRead()
    }

    private fun NotificationDto.toDomain() = NotificationItem(
        id = id,
        title = title,
        body = body,
        type = type,
        createdAt = createdAt,
        orderId = orderId,
        isRead = isRead
    )
}
