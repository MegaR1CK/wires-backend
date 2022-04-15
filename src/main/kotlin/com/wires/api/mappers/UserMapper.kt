package com.wires.api.mappers

import com.wires.api.database.entity.UserEntity
import com.wires.api.extensions.toStringList
import com.wires.api.model.User
import com.wires.api.model.UserPreview
import com.wires.api.routing.respondmodels.UserPreviewResponse
import com.wires.api.routing.respondmodels.UserResponse
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Single
class UserMapper : KoinComponent {

    private val imagesMapper: ImagesMapper by inject()

    fun fromEntityToModel(userEntity: UserEntity) = User(
        id = userEntity.id.value,
        username = userEntity.username,
        avatar = userEntity.avatar?.let { imagesMapper.fromEntityToModel(it) },
        email = userEntity.email,
        passwordHash = userEntity.passwordHash,
        passwordSalt = userEntity.passwordSalt,
        interests = userEntity.interests.toStringList()
    )

    fun fromEntityToPreviewModel(userEntity: UserEntity) = UserPreview(
        id = userEntity.id.value,
        username = userEntity.username,
        avatar = userEntity.avatar?.let { imagesMapper.fromEntityToModel(it) },
    )

    fun fromModelToResponse(user: User) = UserResponse(
        id = user.id,
        email = user.email,
        username = user.username,
        avatar = user.avatar?.let { imagesMapper.fromModelToResponse(it) },
        interests = user.interests
    )

    fun fromModelToResponse(userPreview: UserPreview) = UserPreviewResponse(
        id = userPreview.id,
        username = userPreview.username,
        avatar = userPreview.avatar?.let { imagesMapper.fromModelToResponse(it) },
    )
}
