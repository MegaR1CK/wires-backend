package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.models.Post
import com.wires.api.database.params.PostInsertParams
import com.wires.api.database.tables.Posts
import org.jetbrains.exposed.sql.insert
import org.koin.core.annotation.Single

@Single
class PostsRepository {

    suspend fun getPost(postId: Int): Post? = dbQuery {
        Post.findById(postId)
    }

    suspend fun getPostsList(topics: List<String>, limit: Int, offset: Long) = dbQuery {
        Post
            .find { Posts.topic inList topics }
            .limit(limit, offset)
    }

    suspend fun getUserPosts(userId: Int, limit: Int, offset: Long) = dbQuery {
        Post
            .find { Posts.userId eq userId }
            .limit(limit, offset)
    }

    suspend fun createPost(params: PostInsertParams) = dbQuery {
        Posts.insert { statement ->
            statement[userId] = params.userId
            statement[text] = params.text
            statement[imageUrl] = params.imageUrl
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
            if (listChanged) Post.findById(postId)?.likedUserIds = likedUserIds
        }
    }
}
