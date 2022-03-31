package com.wires.api.database.entity

import com.wires.api.database.tables.Comments
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class CommentEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CommentEntity>(Comments)
    var author by UserEntity referencedOn Comments.userId
    var post by PostEntity referencedOn Comments.postId
    var text by Comments.text
    var sendTime by Comments.sendTime
}
