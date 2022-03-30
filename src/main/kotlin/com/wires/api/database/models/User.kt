package com.wires.api.database.models

import com.wires.api.database.tables.Users
import com.wires.api.extensions.toSeparatedString
import com.wires.api.extensions.toStringList
import com.wires.api.routing.respondmodels.UserPreviewResponse
import com.wires.api.routing.respondmodels.UserResponse
import io.ktor.server.auth.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class User(id: EntityID<Int>) : IntEntity(id), Principal {
    companion object : IntEntityClass<User>(Users)
    var email by Users.email
    var username by Users.username
    var passwordHash by Users.passwordHash
    var passwordSalt by Users.passwordSalt
    var avatarUrl by Users.avatarUrl
    var interests: List<String> by Users.interests.transform(
        toColumn = List<String>::toSeparatedString,
        toReal = String?::toStringList
    )

    fun toResponse() = UserResponse(
        id = id.value,
        email = email,
        username = username,
        avatarUrl = avatarUrl,
        interests = interests
    )

    fun toPreviewResponse() = UserPreviewResponse(
        id = id.value,
        username = username,
        avatarUrl = avatarUrl
    )
}
