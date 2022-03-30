package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.models.Comment
import com.wires.api.database.params.CommentInsertParams
import com.wires.api.database.tables.Comments
import org.jetbrains.exposed.sql.insert
import org.koin.core.annotation.Single

@Single
class CommentsRepository {

    suspend fun addComment(params: CommentInsertParams) = dbQuery {
        Comments.insert { statement ->
            statement[userId] = params.userId
            statement[postId] = params.postId
            statement[text] = params.text
        }
    }

    suspend fun getComments(postId: Int, limit: Int, offset: Long) = dbQuery {
        Comment.find { Comments.postId eq postId }.limit(limit, offset)
    }
}
