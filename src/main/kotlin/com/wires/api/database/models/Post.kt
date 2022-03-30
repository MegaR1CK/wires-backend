package com.wires.api.database.models

import com.wires.api.database.tables.Posts
import com.wires.api.extensions.toInstant
import com.wires.api.extensions.toIntList
import com.wires.api.extensions.toLocalDateTime
import com.wires.api.extensions.toSeparatedString
import com.wires.api.routing.respondmodels.PostResponse
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.time.Instant
import java.time.LocalDateTime

class Post(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Post>(Posts)
    var author by User referencedOn Posts.userId
    var text by Posts.text
    var imageUrl by Posts.imageUrl
    var topic by Posts.topic
    var publishTime: LocalDateTime by Posts.publishTime.transform(
        toColumn = LocalDateTime::toInstant,
        toReal = Instant::toLocalDateTime
    )
    var likedUserIds: List<Int> by Posts.likedUserIds.transform(
        toColumn = List<Int>::toSeparatedString,
        toReal = String?::toIntList
    )
    var commentsCount by Posts.commentsCount

    fun toResponse() = PostResponse(
        id = id.value,
        author = author.toPreviewResponse(),
        text = text,
        imageUrl = imageUrl,
        topic = topic,
        publishTime = publishTime.toString(),
        isUserLiked = likedUserIds.contains(id.value),
        likesCount = likedUserIds.size,
        commentsCount = commentsCount
    )
}
