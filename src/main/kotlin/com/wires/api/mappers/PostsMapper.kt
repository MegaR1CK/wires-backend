package com.wires.api.mappers

import com.wires.api.database.entity.CommentEntity
import com.wires.api.database.entity.PostEntity
import com.wires.api.extensions.toIntList
import com.wires.api.extensions.toLocalDateTime
import com.wires.api.model.Comment
import com.wires.api.model.Post
import com.wires.api.routing.respondmodels.CommentResponse
import com.wires.api.routing.respondmodels.PostResponse
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class PostsMapper : KoinComponent {

    private val userMapper: UserMapper by inject()
    private val imagesMapper: ImagesMapper by inject()

    fun fromEntityToModel(postEntity: PostEntity) = Post(
        id = postEntity.id.value,
        author = userMapper.fromEntityToPreviewModel(postEntity.author),
        text = postEntity.text,
        image = postEntity.image?.let { imagesMapper.fromEntityToModel(it) },
        topic = postEntity.topic,
        publishTime = postEntity.publishTime.toLocalDateTime(),
        likedUserIds = postEntity.likedUserIds.toIntList(),
        commentsCount = postEntity.commentsCount
    )

    fun fromModelToResponse(post: Post) = PostResponse(
        id = post.id,
        author = userMapper.fromModelToResponse(post.author),
        text = post.text,
        image = post.image?.let { imagesMapper.fromModelToResponse(it) },
        topic = post.topic,
        publishTime = post.publishTime.toString(),
        isUserLiked = post.likedUserIds.contains(post.author.id),
        likesCount = post.likedUserIds.size,
        commentsCount = post.commentsCount
    )

    fun fromEntityToModel(commentEntity: CommentEntity) = Comment(
        id = commentEntity.id.value,
        author = userMapper.fromEntityToModel(commentEntity.author),
        postId = commentEntity.post.id.value,
        text = commentEntity.text,
        sendTime = commentEntity.sendTime.toLocalDateTime()
    )

    fun fromModelToResponse(comment: Comment) = CommentResponse(
        id = comment.id,
        author = userMapper.fromModelToResponse(comment.author),
        text = comment.text,
        sendTime = comment.sendTime.toString()
    )
}
