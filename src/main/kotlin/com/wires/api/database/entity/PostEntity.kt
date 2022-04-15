package com.wires.api.database.entity

import com.wires.api.database.tables.Posts
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class PostEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<PostEntity>(Posts)
    var author by UserEntity referencedOn Posts.userId
    var text by Posts.text
    var image by ImageEntity optionalReferencedOn Posts.imageUrl
    var topic by Posts.topic
    var publishTime by Posts.publishTime
    var likedUserIds by Posts.likedUserIds
    var commentsCount by Posts.commentsCount
}
