package com.wires.api.service

import com.wires.api.database.params.CommentInsertParams
import com.wires.api.database.params.ImageInsertParams
import com.wires.api.database.params.PostInsertParams
import com.wires.api.database.params.PostUpdateParams
import com.wires.api.mappers.PostsMapper
import com.wires.api.repository.CommentsRepository
import com.wires.api.repository.ImagesRepository
import com.wires.api.repository.PostsRepository
import com.wires.api.repository.StorageRepository
import com.wires.api.repository.UserRepository
import com.wires.api.routing.ForbiddenException
import com.wires.api.routing.MissingArgumentsException
import com.wires.api.routing.NotFoundException
import com.wires.api.routing.StorageException
import com.wires.api.routing.requestparams.PostCommentParams
import com.wires.api.routing.requestparams.PostCreateParams
import com.wires.api.routing.requestparams.PostEditParams
import com.wires.api.routing.respondmodels.CommentResponse
import com.wires.api.routing.respondmodels.PostResponse
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class PostsService : KoinComponent {

    private val userRepository: UserRepository by inject()
    private val postsRepository: PostsRepository by inject()
    private val storageRepository: StorageRepository by inject()
    private val commentsRepository: CommentsRepository by inject()
    private val imagesRepository: ImagesRepository by inject()
    private val postsMapper: PostsMapper by inject()

    suspend fun getPostsCompilation(userId: Int, limit: Int, offset: Long): List<PostResponse> {
        userRepository.findUserById(userId)?.let { user ->
            return postsRepository.getPostsList(user.interests, limit, offset)
                .map { postsMapper.fromModelToResponse(userId, it) }
        } ?: throw NotFoundException()
    }

    suspend fun getPostsByTopic(userId: Int, topic: String, limit: Int, offset: Long): List<PostResponse> {
        return postsRepository.getPostsList(listOf(topic), limit, offset)
            .map { postsMapper.fromModelToResponse(userId, it) }
    }

    suspend fun createPost(userId: Int, postCreateParams: PostCreateParams?, pictureBytes: ByteArray?) {
        postCreateParams?.let { params ->
            val imageUrl = pictureBytes?.let { bytes ->
                val image = storageRepository.uploadFile(bytes) ?: throw StorageException()
                imagesRepository.addImage(ImageInsertParams(image.url, image.size.width, image.size.height))
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

    suspend fun updatePost(userId: Int, postId: Int, postUpdateParams: PostEditParams?, imageBytes: ByteArray?) {
        postUpdateParams?.let { params ->
            val post = postsRepository.getPost(postId) ?: throw NotFoundException()
            if (post.author.id != userId) throw ForbiddenException()
            val imageUrl = imageBytes?.let { bytes ->
                if (bytes.isNotEmpty()) {
                    val image = storageRepository.uploadFile(bytes) ?: throw StorageException()
                    imagesRepository.addImage(ImageInsertParams(image.url, image.size.width, image.size.height)).value
                } else {
                    ""
                }
            }
            val updateParams = PostUpdateParams(
                id = postId,
                text = params.text,
                topic = params.topic,
                imageUrl = imageUrl
            )
            postsRepository.updatePost(updateParams)
        } ?: throw MissingArgumentsException()
    }

    suspend fun deletePost(userId: Int, postId: Int) {
        val post = postsRepository.getPost(postId) ?: throw NotFoundException()
        if (post.author.id != userId) throw ForbiddenException()
        commentsRepository.deletePostComments(postId)
        postsRepository.deletePost(postId)
    }

    suspend fun getPost(userId: Int, postId: Int): PostResponse {
        postsRepository.getPost(postId)?.let { post ->
            return postsMapper.fromModelToResponse(userId, post)
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
            postsRepository.increasePostCommentCounter(postId)
        } ?: throw NotFoundException()
    }

    suspend fun getPostComments(postId: Int, limit: Int, offset: Long): List<CommentResponse> {
        postsRepository.getPost(postId)?.let {
            return commentsRepository.getComments(postId, limit, offset).map(postsMapper::fromModelToResponse)
        } ?: throw NotFoundException()
    }
}
