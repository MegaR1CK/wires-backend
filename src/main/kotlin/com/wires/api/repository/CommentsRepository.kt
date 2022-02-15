package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.params.CommentInsertParams
import com.wires.api.database.tables.Comments
import org.jetbrains.exposed.sql.insert

class CommentsRepository {

    suspend fun addComment(params: CommentInsertParams) = dbQuery {
        Comments.insert { statement ->
            statement[userId] = params.userId
            statement[postId] = params.postId
            statement[text] = params.text
        }
    }
}
