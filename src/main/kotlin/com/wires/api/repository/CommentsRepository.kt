package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.params.CommentInsertParams
import com.wires.api.database.tables.Comments
import com.wires.api.extensions.toComment
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class CommentsRepository {

    suspend fun addComment(params: CommentInsertParams) = dbQuery {
        Comments.insert { statement ->
            statement[userId] = params.userId
            statement[postId] = params.postId
            statement[text] = params.text
        }
    }

    suspend fun getComments(postId: Int) = dbQuery {
        Comments.select { Comments.postId.eq(postId) }.mapNotNull { it.toComment() }
    }
}
