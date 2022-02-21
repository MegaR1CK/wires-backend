package com.wires.api.extensions

import com.wires.api.database.models.*
import com.wires.api.database.tables.*
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow?.toUser(): User? {
    return this?.let { row ->
        User(
            id = row[Users.id].value,
            username = row[Users.username],
            email = row[Users.email],
            passwordHash = row[Users.passwordHash],
            passwordSalt = row[Users.passwordSalt],
            avatarUrl = row[Users.avatarUrl],
            interests = row[Users.interests].toStringArray(),
            channels = row[Users.channels].toIntArray()
        )
    }
}

fun ResultRow?.toPost(): Post? {
    return this?.let { row ->
        Post(
            id = row[Posts.id].value,
            userId = row[Posts.userId].value,
            text = row[Posts.text],
            imageUrl = row[Posts.imageUrl],
            topic = row[Posts.topic],
            publishTime = row[Posts.publishTime].toLocalDateTime(),
            likedUserIds = row[Posts.likedUserIds].toIntArray(),
            commentsCount = row[Posts.commentsCount]
        )
    }
}

fun ResultRow?.toComment(): Comment? {
    return this?.let { row ->
        Comment(
            id = row[Comments.id].value,
            userId = row[Comments.userId].value,
            postId = row[Comments.postId].value,
            text = row[Comments.text],
            sendTime = row[Comments.sendTime].toLocalDateTime()
        )
    }
}

fun ResultRow?.toChannel(): Channel? {
    return this?.let { row ->
        Channel(
            id = row[Channels.id].value,
            name = row[Channels.name],
            imageUrl = row[Channels.imageUrl],
            membersIds = row[Channels.membersIds].toIntArray()
        )
    }
}

fun ResultRow?.toMessage(): Message? {
    return this?.let { row ->
        Message(
            id = row[Messages.id].value,
            userId = row[Messages.userId].value,
            channelId = row[Messages.channelId].value,
            text = row[Messages.text],
            sendTime = row[Messages.sendTime].toLocalDateTime()
        )
    }
}
