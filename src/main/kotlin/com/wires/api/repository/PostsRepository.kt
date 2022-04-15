package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.entity.PostEntity
import com.wires.api.database.params.PostInsertParams
import com.wires.api.database.tables.Posts
import com.wires.api.extensions.toSeparatedString
import com.wires.api.mappers.PostsMapper
import com.wires.api.model.Post
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class PostsRepository : KoinComponent {

    private val postsMapper: PostsMapper by inject()

    suspend fun getPost(postId: Int): Post? = dbQuery {
        PostEntity
            .findById(postId)
            ?.let(postsMapper::fromEntityToModel)
    }

    suspend fun getPostsList(topics: List<String>, limit: Int, offset: Long): List<Post> = dbQuery {
        PostEntity
            .find { Posts.topic inList topics }
            .orderBy(Posts.publishTime to SortOrder.DESC)
            .limit(limit, offset)
            .map(postsMapper::fromEntityToModel)
    }

    suspend fun getUserPosts(userId: Int, limit: Int, offset: Long): List<Post> = dbQuery {
        PostEntity
            .find { Posts.userId eq userId }
            .orderBy(Posts.publishTime to SortOrder.DESC)
            .limit(limit, offset)
            .map(postsMapper::fromEntityToModel)
    }

    suspend fun createPost(params: PostInsertParams) = dbQuery {
        Posts.insert { statement ->
            statement[userId] = params.userId
            statement[text] = params.text
            params.imageUrl?.let { statement[imageUrl] = it }
            statement[topic] = params.topic
        }
    }

    suspend fun likePost(userId: Int, postId: Int, isLiked: Boolean) {
        val likedUserIds = getPost(postId)?.likedUserIds?.toMutableList() ?: mutableListOf()
        var listChanged = true
        dbQuery {
            when {
                isLiked && !likedUserIds.contains(userId) -> likedUserIds.add(userId)
                !isLiked && likedUserIds.contains(userId) -> likedUserIds.remove(userId)
                else -> listChanged = false
            }
            if (listChanged) PostEntity.findById(postId)?.likedUserIds = likedUserIds.toList().toSeparatedString()
        }
    }

    suspend fun increasePostCommentCounter(postId: Int): Unit = dbQuery {
        PostEntity.findById(postId)?.let { post ->
            post.commentsCount = post.commentsCount.inc()
        }
    }
}
