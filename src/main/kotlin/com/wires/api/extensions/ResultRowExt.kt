package com.wires.api.extensions

import com.wires.api.database.models.Post
import com.wires.api.database.models.User
import com.wires.api.database.tables.Posts
import com.wires.api.database.tables.Users
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow?.toUser(): User? {
    return this?.let { row ->
        User(
            id = row[Users.id].value,
            username = row[Users.username],
            email = row[Users.email],
            passwordHash = row[Users.passwordHash],
            passwordSalt = row[Users.passwordSalt]
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
            publishTime = row[Posts.publishTime],
            likedUserIds = row[Posts.likedUserIds].toIntArray(),
            commentsCount = row[Posts.commentsCount]
        )
    }
}
