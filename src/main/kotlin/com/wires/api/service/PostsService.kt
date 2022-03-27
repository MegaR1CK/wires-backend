package com.wires.api.service

import com.wires.api.database.params.CommentInsertParams
import com.wires.api.database.params.PostInsertParams
import com.wires.api.repository.CommentsRepository
import com.wires.api.repository.PostsRepository
import com.wires.api.repository.StorageRepository
import com.wires.api.repository.UserRepository
import com.wires.api.routing.MissingArgumentsException
import com.wires.api.routing.NotFoundException
import com.wires.api.routing.StorageException
import com.wires.api.routing.requestparams.PostCommentParams
import com.wires.api.routing.requestparams.PostCreateParams
import com.wires.api.routing.respondmodels.CommentResponse
import com.wires.api.routing.respondmodels.PostResponse
import com.wires.api.utils.DateFormatter
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class PostsService : KoinComponent {

    private val userRepository: UserRepository by inject()
    private val postsRepository: PostsRepository by inject()
    private val storageRepository: StorageRepository by inject()
    private val commentsRepository: CommentsRepository by inject()
    private val dateFormatter: DateFormatter by inject()

    suspend fun getPostsCompilation(userId: Int, limit: Int, offset: Long): List<PostResponse> {
        userRepository.findUserById(userId)?.let { user ->
            return postsRepository.getPostsList(user.interests, limit, offset).map { post ->
                post.toResponse(
                    userRepository.findUserById(post.userId)?.toPreviewResponse(),
                    dateFormatter.dateTimeToFullString(post.publishTime)
                )
            }
        } ?: throw NotFoundException()
    }

    suspend fun getPostsByTopic(topic: String, limit: Int, offset: Long): List<PostResponse> {
        return postsRepository.getPostsList(listOf(topic), limit, offset).map { post ->
            post.toResponse(
                userRepository.findUserById(post.userId)?.toPreviewResponse(),
                dateFormatter.dateTimeToFullString(post.publishTime)
            )
        }
    }

    suspend fun createPost(userId: Int, postCreateParams: PostCreateParams?, pictureBytes: ByteArray?) {
        postCreateParams?.let { params ->
            val imageUrl = pictureBytes?.let { bytes ->
                storageRepository.uploadFile(bytes) ?: throw StorageException()
            }
            val insertParams = PostInsertParams(
                text = params.text,
                imageUrl = imageUrl,
                topic = params.topic,
                userId = userId
            )
            postsRepository.createPost(insertParams)
        } ?: throw MissingArgumentsException()
    }

    suspend fun getPost(postId: Int): PostResponse {
        postsRepository.getPost(postId)?.let { post ->
            return post.toResponse(
                userRepository.findUserById(post.userId)?.toPreviewResponse(),
                dateFormatter.dateTimeToFullString(post.publishTime)
            )
        } ?: throw NotFoundException()
    }

    suspend fun likePost(userId: Int, postId: Int, isLiked: Boolean) {
        postsRepository.getPost(postId)?.let {
            postsRepository.likePost(userId, postId, isLiked)
        } ?: throw NotFoundException()
    }

    suspend fun commentPost(userId: Int, postId: Int, postCommentParams: PostCommentParams) {
        postsRepository.getPost(postId)?.let {
            commentsRepository.addComment(
                CommentInsertParams(
                    userId = userId,
                    postId = postId,
                    text = postCommentParams.text
                )
            )
        } ?: throw NotFoundException()
    }

    suspend fun getPostComments(postId: Int, limit: Int, offset: Long): List<CommentResponse> {
        postsRepository.getPost(postId)?.let {
            return commentsRepository.getComments(postId, limit, offset)
                .map { comment ->
                    comment.toResponse(
                        userRepository.findUserById(comment.userId)?.toPreviewResponse(),
                        dateFormatter.dateTimeToFullString(comment.sendTime)
                    )
                }
        } ?: throw NotFoundException()
    }
}
