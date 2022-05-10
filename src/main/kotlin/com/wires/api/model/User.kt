package com.wires.api.model

data class User(
    override val id: Int,
    override val username: String,
    override val avatar: Image?,
    override val firstName: String?,
    override val lastName: String?,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    val interests: List<String>,
) : UserPreview(id, username, avatar, firstName, lastName)
