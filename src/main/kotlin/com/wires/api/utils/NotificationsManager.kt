package com.wires.api.utils

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import org.koin.core.annotation.Single

@Single
class NotificationsManager {

    companion object {
        private const val NOTIFICATION_TITLE_KEY = "title"
        private const val NOTIFICATION_BODY_KEY = "body"
        private const val NOTIFICATION_IMAGE_KEY = "image"
    }

    fun sendNewMessageNotifications(title: String, body: String, imageUrl: String, pushTokens: List<String>) {
        val message = MulticastMessage.builder()
            .putData(NOTIFICATION_TITLE_KEY, title)
            .putData(NOTIFICATION_BODY_KEY, body)
            .putData(NOTIFICATION_IMAGE_KEY, imageUrl)
            .addAllTokens(pushTokens)
            .build()
        FirebaseMessaging.getInstance().sendMulticast(message)
    }
}
