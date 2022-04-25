package com.wires.api.database.params

import org.jetbrains.exposed.dao.id.EntityID

data class UserUpdateParams(
    val id: Int,
    val username: String?,
    val email: String?,
    val avatarUrl: EntityID<String>?,
    val interests: List<String>?
)
