package com.wires.api.utils

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.MulticastMessage
import org.koin.core.annotation.Single

@Single
class NotificationsManager {

    companion object {
        private const val NOTIFICATION_TITLE_KEY = "title"
        private const val NOTIFICATION_BODY_KEY = "body"
    }

    fun sendNewMessageNotifications(title: String, body: String, pushTokens: List<String>) {
        val message = MulticastMessage.builder()
            .putData(NOTIFICATION_TITLE_KEY, title)
            .putData(NOTIFICATION_BODY_KEY, body)
            .addAllTokens(pushTokens)
            .build()
        FirebaseMessaging.getInstance().sendMulticast(message)
    }
}
