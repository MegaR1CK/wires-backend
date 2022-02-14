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

class PostsRepository {

    suspend fun getPost(id: Int): Post? = dbQuery {
        return@dbQuery Posts.select { Posts.id.eq(id) }.map { it.toPost() }.singleOrNull()
    }

    suspend fun getPostsList(topic: String): List<Post> = dbQuery {
        return@dbQuery Posts.select { Posts.topic.eq(topic) }.mapNotNull { it.toPost() }
    }

    suspend fun getUserPosts(userId: Int): List<Post> = dbQuery {
        return@dbQuery Posts.select { Posts.userId.eq(userId) }.mapNotNull { it.toPost() }
    }

    suspend fun createPost(params: PostInsertParams) = dbQuery {
        Posts.insert { statement ->
            statement[userId] = params.userId
            statement[text] = params.text
            statement[imageUrl] = params.imageUrl
        }
    }

    suspend fun likePost(userId: Int, postId: Int, isLiked: Boolean) {
        val likedUserIds = getPost(postId)?.likedUserIds?.toMutableList() ?: mutableListOf()
        dbQuery {
            Posts.update({ Posts.id.eq(postId) }) {
                when {
                    !isLiked && !likedUserIds.contains(userId) -> likedUserIds.add(userId)
                    isLiked && likedUserIds.contains(userId) -> likedUserIds.remove(userId)
                }
                it[Posts.likedUserIds] = likedUserIds.toIntArray().toSeparatedString()
            }
        }
    }
}
