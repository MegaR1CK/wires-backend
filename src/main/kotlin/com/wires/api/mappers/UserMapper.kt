package com.wires.api.mappers

import com.wires.api.database.entity.UserEntity
import com.wires.api.extensions.toStringList
import com.wires.api.model.User
import com.wires.api.model.UserPreview
import com.wires.api.routing.respondmodels.UserPreviewResponse
import com.wires.api.routing.respondmodels.UserResponse
import org.koin.core.annotation.Single

@Single
class UserMapper {

    fun fromEntityToModel(userEntity: UserEntity) = User(
        id = userEntity.id.value,
        username = userEntity.username,
        avatarUrl = userEntity.avatarUrl,
        email = userEntity.email,
        passwordHash = userEntity.passwordHash,
        passwordSalt = userEntity.passwordSalt,
        interests = userEntity.interests.toStringList()
    )

    fun fromEntityToPreviewModel(userEntity: UserEntity) = UserPreview(
        id = userEntity.id.value,
        username = userEntity.username,
        avatarUrl = userEntity.avatarUrl
    )

    fun fromModelToResponse(user: User) = UserResponse(
        id = user.id,
        email = user.email,
        username = user.username,
        avatarUrl = user.avatarUrl,
        interests = user.interests
    )

    fun fromModelToResponse(userPreview: UserPreview) = UserPreviewResponse(
        id = userPreview.id,
        username = userPreview.username,
        avatarUrl = userPreview.avatarUrl
    )
}
