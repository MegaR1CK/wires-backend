package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.entity.CommentEntity
import com.wires.api.database.params.CommentInsertParams
import com.wires.api.database.tables.Comments
import com.wires.api.mappers.PostsMapper
import com.wires.api.model.Comment
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class CommentsRepository : KoinComponent {

    private val postsMapper: PostsMapper by inject()

    suspend fun addComment(params: CommentInsertParams) = dbQuery {
        Comments.insert { statement ->
            statement[userId] = params.userId
            statement[postId] = params.postId
            statement[text] = params.text
        }
    }

    suspend fun getComments(postId: Int, limit: Int, offset: Long): List<Comment> = dbQuery {
        CommentEntity
            .find { Comments.postId eq postId }
            .orderBy(Comments.sendTime to SortOrder.DESC)
            .limit(limit, offset)
            .map(postsMapper::fromEntityToModel)
    }

    suspend fun deletePostComments(postId: Int) = dbQuery {
        CommentEntity.find { Comments.postId eq postId }.forEach { it.delete() }
    }
}
