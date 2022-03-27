package com.wires.api.repository

import com.wires.api.database.dbQuery
import com.wires.api.database.models.Post
import com.wires.api.database.params.PostInsertParams
import com.wires.api.database.tables.Posts
import com.wires.api.extensions.toPost
import com.wires.api.extensions.toSeparatedString
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import org.koin.core.annotation.Single

@Single
class PostsRepository {

    suspend fun getPost(id: Int): Post? = dbQuery {
        Posts
            .select { Posts.id.eq(id) }
            .map { it.toPost() }
            .singleOrNull()
    }

    suspend fun getPostsList(topics: List<String>, limit: Int, offset: Long): List<Post> = dbQuery {
        Posts
            .select { Posts.topic.inList(topics) }
            .limit(limit, offset)
            .mapNotNull { it.toPost() }
    }

    suspend fun getUserPosts(userId: Int, limit: Int, offset: Long): List<Post> = dbQuery {
        Posts
            .select { Posts.userId.eq(userId) }
            .limit(limit, offset)
            .mapNotNull { it.toPost() }
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
            if (listChanged) {
                Posts.update({ Posts.id.eq(postId) }) {
                    it[Posts.likedUserIds] = likedUserIds.toTypedArray().toSeparatedString()
                }
            }
        }
    }
}
